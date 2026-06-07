package com.github.geovanegsfarias.mapper;

import com.github.geovanegsfarias.dto.response.OrderItemResponse;
import com.github.geovanegsfarias.model.OrderItem;

public class OrderItemMapper {

    public static OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        return new OrderItemResponse(
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getPrice(),
                orderItem.getQuantity()
        );
    }

}
