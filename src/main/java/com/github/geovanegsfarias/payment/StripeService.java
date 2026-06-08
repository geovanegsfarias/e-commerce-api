package com.github.geovanegsfarias.payment;

import com.github.geovanegsfarias.exception.IllegalOperationException;
import com.github.geovanegsfarias.exception.InsufficientStockException;
import com.github.geovanegsfarias.exception.PaymentException;
import com.github.geovanegsfarias.exception.ResourceNotFoundException;
import com.github.geovanegsfarias.order.OrderItem;
import com.github.geovanegsfarias.order.OrderRepository;
import com.github.geovanegsfarias.order.OrderStatus;
import com.github.geovanegsfarias.product.ProductRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class StripeService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public StripeService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public CheckoutResponse checkoutOrder(CheckoutRequest request, String email) {
        var order = orderRepository.findByIdAndUserEmail(request.orderId(), email).orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalOperationException("Only pending orders can be checked out");
        }

        for (OrderItem orderItem : order.getOrderItems()) {
            if (orderItem.getProduct().getStock() < orderItem.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock");
            }
        }

        SessionCreateParams.LineItem.PriceData.ProductData orderDetails =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName("Order #" + order.getId())
                        .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("USD")
                        .setUnitAmount(order.getPrice().multiply(new BigDecimal("100")).longValue())
                        .setProductData(orderDetails)
                        .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams
                        .LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(priceData)
                        .build();

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("http://localhost:8080")
                        .setCancelUrl("http://localhost:8080")
                        .setClientReferenceId(order.getId().toString())
                        .addLineItem(lineItem)
                        .build();

        Session session = null;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            log.warn("Failed to create payment session: {}", e.getMessage());
            throw new PaymentException("Failed to create payment session");
        }

        return new CheckoutResponse("SUCCESS", "Payment session created", session.getId(), session.getUrl());
    }

    @Transactional
    public void handlePaymentSuccess(Session session) {
        String orderId = session.getClientReferenceId();
        if (orderId == null) return;

        orderRepository.findById(Long.parseLong(orderId)).ifPresent(order -> {
            if (order.getStatus() == OrderStatus.PAID) return;

            order.setStatus(OrderStatus.PAID);
            order.getOrderItems().forEach(orderItem -> {
                orderItem.getProduct().setStock(
                        orderItem.getProduct().getStock() - orderItem.getQuantity()
                );
                productRepository.save(orderItem.getProduct());
            });
            order.setStripePaymentIntentId(session.getPaymentIntent());
            orderRepository.save(order);
            log.info("Payment completed for order with id {}", order.getId());
        });
    }

    @Transactional
    public void handlePaymentFailed(Session session) {
        var orderId = session.getClientReferenceId();
        if (orderId == null) return;

        orderRepository.findById(Long.parseLong(orderId)).ifPresent(order -> {
            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);
            log.warn("Payment failed for order with id {}", order.getId());
        });
    }

}
