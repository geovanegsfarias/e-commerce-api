package com.geovane.e_commerce_api.service;

import com.geovane.e_commerce_api.dto.request.StripeRequest;
import com.geovane.e_commerce_api.dto.response.StripeResponse;
import com.geovane.e_commerce_api.exception.*;
import com.geovane.e_commerce_api.model.Order;
import com.geovane.e_commerce_api.model.OrderItem;
import com.geovane.e_commerce_api.model.OrderStatus;
import com.geovane.e_commerce_api.repository.OrderRepository;
import com.geovane.e_commerce_api.repository.ProductRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class StripeService {
    private static final Logger logger = LoggerFactory.getLogger(StripeService.class);
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Autowired
    public StripeService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public StripeResponse checkoutOrder(StripeRequest request, String email) {
        Order order = orderRepository.findByIdAndUserEmail(request.orderId(), email).orElseThrow(() -> new ResourceNotFoundException("Order not found."));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalOperationException("This order has already been completed.");
        }

        for (OrderItem orderItem : order.getOrderItems()) {
            if (orderItem.getProduct().getStock() < orderItem.getQuantity()) {
                throw new InsufficientStockException("Insufficient stock.");
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
            throw new PaymentException("Payment session creation failed: " + e.getMessage());
        }

        return new StripeResponse("SUCCESS", "Payment session created.", session.getId(), session.getUrl());
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
            logger.info("Order #{} payment completed.", order.getId());
        });
    }

    @Transactional
    public void handlePaymentFailed(Session session) {
        String orderId = session.getClientReferenceId();
        if (orderId == null) return;

        orderRepository.findById(Long.parseLong(orderId)).ifPresent(order -> {
            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);
            logger.info("Order #{} payment failed.", order.getId());
        });
    }

}
