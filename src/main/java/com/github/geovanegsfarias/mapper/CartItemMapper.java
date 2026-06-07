package com.github.geovanegsfarias.mapper;

import com.github.geovanegsfarias.dto.response.CartItemResponse;
import com.github.geovanegsfarias.model.Cart;
import com.github.geovanegsfarias.model.CartItem;
import com.github.geovanegsfarias.model.Product;

public class CartItemMapper {

    public static CartItem toCartItem(Cart cart, Product product, int quantity) {
        return new CartItem(
                quantity,
                cart,
                product
        );
    }

    public static CartItemResponse toCartItemResponse(CartItem cartItem) {
        return new CartItemResponse(
                cartItem.getId(),
                cartItem.getProduct().getId(),
                cartItem.getProduct().getName(),
                cartItem.getProduct().getPrice(),
                cartItem.getQuantity()
        );
    }

}