package com.github.geovanegsfarias.order;

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
