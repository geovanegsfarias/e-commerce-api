package com.github.geovanegsfarias.order;

import com.github.geovanegsfarias.cart.CartItem;
import com.github.geovanegsfarias.cart.CartItemRepository;
import com.github.geovanegsfarias.cart.CartService;
import com.github.geovanegsfarias.exception.EmptyCartException;
import com.github.geovanegsfarias.exception.IllegalOperationException;
import com.github.geovanegsfarias.exception.InsufficientStockException;
import com.github.geovanegsfarias.exception.ResourceNotFoundException;
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
    private final CartService cartService;
    private final CartItemRepository cartItemRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, CartService cartService, CartItemRepository cartItemRepository) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.cartItemRepository = cartItemRepository;
    }

    public Page<Order> findAll(String userEmail, Pageable pageable) {
        return orderRepository.findAllByUserEmail(userEmail, pageable);
    }

    public Order findByIdAndEmailOrThrowException(Long id, String userEmail) {
        return orderRepository.findByIdAndUserEmail(id, userEmail).orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Transactional
    public Order save(String userEmail) {
        var cart = cartService.findByEmailOrThrowException(userEmail);
        var cartItems = cart.getCartItems();
        assertCartIsNotEmpty(cartItems);
        var orderTotalPrice = sumTotalPrice(cartItems);
        var order = new Order(orderTotalPrice, cart.getUser(), null);
        var orderItems = createOrderItems(cartItems, order);
        order.setOrderItems(orderItems);
        var savedOrder = orderRepository.save(order);
        cartItemRepository.deleteAllByCartId(cart.getId());
        return savedOrder;
    }

    public void delete(Long id, String userEmail) {
        var orderToDelete = findByIdAndEmailOrThrowException(id, userEmail);
        assertOrderIsPending(orderToDelete);
        orderRepository.delete(orderToDelete);
    }

    private void assertCartIsNotEmpty(List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) {
            throw new EmptyCartException("Your cart is empty");
        }
    }

    private void assertStockIsAvailable(CartItem cartItem) {
        if (cartItem.getProduct().getStock() < cartItem.getQuantity()) {
            throw new InsufficientStockException("Insufficient stock");
        }
    }

    private void assertOrderIsPending(Order order) {
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new IllegalOperationException("Only pending orders can be deleted");
        }
    }

    private BigDecimal sumTotalPrice(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(cartItem -> cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add); // (sum, nextValue) -> sum.add(nextValue)
    }

    private List<OrderItem> createOrderItems(List<CartItem> cartItems, Order order) {
        return cartItems.stream()
                .map(cartItem -> {
                            assertStockIsAvailable(cartItem);

                            return new OrderItem(
                                    cartItem.getProduct().getPrice(),
                                    cartItem.getQuantity(),
                                    order, cartItem.getProduct()
                            );
                        })
                .toList();
    }

}
