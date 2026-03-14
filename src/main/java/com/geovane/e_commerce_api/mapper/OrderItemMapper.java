package com.geovane.e_commerce_api.mapper;

import com.geovane.e_commerce_api.dto.response.OrderItemResponse;
import com.geovane.e_commerce_api.model.OrderItem;

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
