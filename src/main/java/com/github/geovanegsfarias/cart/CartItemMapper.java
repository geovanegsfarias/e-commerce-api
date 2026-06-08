package com.github.geovanegsfarias.cart;

import com.github.geovanegsfarias.product.Product;

public class CartItemMapper {

    public static CartItem toCartItem(Cart cart, Product product, int quantity) {
        return new CartItem(
                quantity,
                cart,
                product
        );
    }

    public static CartItem toCartItem(CreateCartItemRequest request) {
        return new CartItem(
                request.quantity()
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