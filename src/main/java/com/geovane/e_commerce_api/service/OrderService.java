package com.geovane.e_commerce_api.service;

import com.geovane.e_commerce_api.dto.response.OrderResponse;
import com.geovane.e_commerce_api.exception.*;
import com.geovane.e_commerce_api.mapper.OrderMapper;
import com.geovane.e_commerce_api.model.Cart;
import com.geovane.e_commerce_api.model.Order;
import com.geovane.e_commerce_api.model.OrderItem;
import com.geovane.e_commerce_api.model.OrderStatus;
import com.geovane.e_commerce_api.repository.CartItemRepository;
import com.geovane.e_commerce_api.repository.CartRepository;
import com.geovane.e_commerce_api.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public Page<OrderResponse> getAll(String email, Pageable pageable) {
        return orderRepository.findAllByUserEmail(email, pageable)
                .map(order -> OrderMapper.toOrderResponse(order));
    }

    public OrderResponse getByIdAndEmail(Long id, String email) {
        return OrderMapper.toOrderResponse(
                orderRepository.findByIdAndUserEmail(id, email).orElseThrow(() -> new ResourceNotFoundException("Order not found."))
        );
    }

    @Transactional
    public OrderResponse save(String email) {
        Cart cart = cartRepository.findByUserEmail(email).orElseThrow(() -> new ResourceNotFoundException("Cart not found."));

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new EmptyCartException("Your cart is empty.");
        }

        BigDecimal totalPrice = cart.getCartItems().stream()
                .map(cartItem -> cartItem.getProduct().getPrice().multiply(new BigDecimal(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, (sum, nextValue) -> sum.add(nextValue));

        Order order = new Order(totalPrice, cart.getUser(), null);

        List<OrderItem> orderItems = cart.getCartItems().stream()
                .map(cartItem -> {
                    if (cartItem.getProduct().getStock() < cartItem.getQuantity()) throw new InsufficientStockException("Insufficient stock.");
                    return new OrderItem(cartItem.getProduct().getPrice(), cartItem.getQuantity(), order, cartItem.getProduct());
                }).toList();

        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        cartItemRepository.deleteAllByCartId(cart.getId());
        return OrderMapper.toOrderResponse(savedOrder);
    }

    public void delete(Long id, String email) {
        Order order = orderRepository.findByIdAndUserEmail(id, email).orElseThrow(() -> new ResourceNotFoundException("Order not found."));
        if (order.getStatus() != OrderStatus.PENDING) throw new IllegalOperationException("Only pending orders can be deleted.");
        orderRepository.delete(order);
    }

}
