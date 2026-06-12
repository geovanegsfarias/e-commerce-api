package com.github.geovanegsfarias.commons;

import com.github.geovanegsfarias.order.Order;
import com.github.geovanegsfarias.order.OrderItem;
import com.github.geovanegsfarias.order.OrderStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Component
public class OrderUtils {
    private final UserUtils userUtils;
    private final ProductUtils productUtils;

    public OrderUtils(UserUtils userUtils, ProductUtils productUtils) {
        this.userUtils = userUtils;
        this.productUtils = productUtils;
    }

    public Order savedOrder() {
        var user = userUtils.savedUser();

        var order = Order.builder()
                .id(1L)
                .price(BigDecimal.valueOf(1499.50))
                .date(Instant.parse("2026-06-11T21:00:00Z"))
                .user(user)
                .status(OrderStatus.PENDING)
                .build();

        order.setOrderItems(savedOrderItemList(order));

        return order;
    }

    public List<OrderItem> savedOrderItemList(Order order) {
        var product = productUtils.savedProduct();

        var orderItem = OrderItem.builder()
                .id(1L)
                .price(product.getPrice())
                .quantity(5)
                .order(order)
                .product(product)
                .build();

        return List.of(orderItem);
    }

}