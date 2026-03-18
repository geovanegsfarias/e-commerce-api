package com.geovane.e_commerce_api.service;

import com.geovane.e_commerce_api.dto.response.OrderResponse;
import com.geovane.e_commerce_api.exception.EmptyCartException;
import com.geovane.e_commerce_api.exception.IllegalOperationException;
import com.geovane.e_commerce_api.exception.InsufficientStockException;
import com.geovane.e_commerce_api.exception.ResourceNotFoundException;
import com.geovane.e_commerce_api.model.*;
import com.geovane.e_commerce_api.repository.CartItemRepository;
import com.geovane.e_commerce_api.repository.CartRepository;
import com.geovane.e_commerce_api.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldReturnAllOrders() {
        User user = new User("user", "user@gmail.com", "password");
        Page<Order> orders = new PageImpl<>(List.of(new Order(BigDecimal.valueOf(1L), user, List.of())));
        
        Mockito.when(orderRepository.findAllByUserEmail("user@gmail.com", Pageable.unpaged())).thenReturn(orders);

        Page<OrderResponse> returnedOrders = orderService.getAll("user@gmail.com", Pageable.unpaged());

        assertThat(returnedOrders.getContent()).hasSize(orders.getContent().size());
    }

    @Test
    void shouldReturnOrderByOrderIdAndUserEmail() {
        User user = new User("User", "user@gmail.com", "password");
        Order order = new Order(BigDecimal.valueOf(100L), user, List.of());

        Mockito.when(orderRepository.findByIdAndUserEmail(1L, user.getEmail())).thenReturn(Optional.of(order));

        OrderResponse returnedOrder = orderService.getByIdAndEmail(1L, user.getEmail());

        assertThat(returnedOrder.totalPrice()).isEqualTo(order.getPrice());
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFoundOnGet() {
        Mockito.when(orderRepository.findByIdAndUserEmail(1L, "user@gmail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getByIdAndEmail(1L, "user@gmail.com")).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldReturnSavedOrder() {
        User user = new User("User", "user@gmail.com", "password");
        Product product = new Product("Product", "Description", BigDecimal.valueOf(100), 10, new Category("Category"));
        Cart cart = new Cart(user);
        cart.setCartItems(List.of(new CartItem(2, cart, product)));
        Order order = new Order(BigDecimal.valueOf(100), user, List.of());

        Mockito.when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(cart));
        Mockito.when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponse returnedOrder = orderService.save(user.getEmail());

        assertThat(returnedOrder.totalPrice()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }

    @Test
    void shouldThrowExceptionWhenCartNotFoundOnPost() {
        Mockito.when(cartRepository.findByUserEmail("user@gmail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.save("user@gmail.com")).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenCartIsEmpty() {
        User user = new User("User", "user@gmail.com", "password");
        Cart cart = new Cart(user);
        cart.setCartItems(List.of());

        Mockito.when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> orderService.save(user.getEmail())).isInstanceOf(EmptyCartException.class);
    }

    @Test
    void shouldThrowExceptionWhenStockIsInsufficient() {
        User user = new User("User", "user@gmail.com", "password");
        Product product = new Product("Product", "Description", BigDecimal.valueOf(100), 2, new Category("Category"));
        Cart cart = new Cart(user);
        cart.setCartItems(List.of(new CartItem(5, cart, product)));

        Mockito.when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> orderService.save(user.getEmail())).isInstanceOf(InsufficientStockException.class);
    }

    @Test
    void shouldDeleteOrder() {
        User user = new User("User", "user@gmail.com", "password");
        List<OrderItem> orderItems = List.of();
        Order order = new Order(BigDecimal.valueOf(100L), user, orderItems);

        Mockito.when(orderRepository.findByIdAndUserEmail(1L, user.getEmail())).thenReturn(Optional.of(order));

        orderService.delete(1L, user.getEmail());

        Mockito.verify(orderRepository).delete(order);
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFoundOnDelete() {
        Mockito.when(orderRepository.findByIdAndUserEmail(1L, "user@gmail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.delete(1L, "user@gmail.com")).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenOrderIsNotPendingOnDelete() {
        User user = new User("User", "user@gmail.com", "password");
        List<OrderItem> orderItems = List.of();
        Order order = new Order(BigDecimal.valueOf(100L), user, orderItems);
        order.setStatus(OrderStatus.PAID);

        Mockito.when(orderRepository.findByIdAndUserEmail(1L, user.getEmail())).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.delete(1L, "user@gmail.com")).isInstanceOf(IllegalOperationException.class);
    }

}