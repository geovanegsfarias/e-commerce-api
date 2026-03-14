package com.geovane.e_commerce_api.mapper;

import com.geovane.e_commerce_api.dto.response.CartItemResponse;
import com.geovane.e_commerce_api.model.Cart;
import com.geovane.e_commerce_api.model.CartItem;
import com.geovane.e_commerce_api.model.Product;

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