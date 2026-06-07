package com.github.geovanegsfarias.mapper;

import com.github.geovanegsfarias.dto.response.OrderResponse;
import com.github.geovanegsfarias.model.Order;

public class OrderMapper {

    public static OrderResponse toOrderResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getStatus(),
                order.getDate(),
                order.getOrderItems().stream().map(orderItem -> OrderItemMapper.toOrderItemResponse(orderItem)).toList(),
                order.getPrice()
        );
    }

}
