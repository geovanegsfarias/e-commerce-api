package com.geovane.e_commerce_api.mapper;

import com.geovane.e_commerce_api.dto.response.OrderResponse;
import com.geovane.e_commerce_api.model.Order;

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
