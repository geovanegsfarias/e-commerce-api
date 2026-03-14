package com.geovane.e_commerce_api.mapper;

import com.geovane.e_commerce_api.dto.response.CartResponse;
import com.geovane.e_commerce_api.model.Cart;
import com.geovane.e_commerce_api.model.User;

import java.math.BigDecimal;

public class CartMapper {

    public static Cart toCart(User user) {
        return new Cart(
                user
        );
    }

    public static CartResponse toCartResponse(Cart cart) {
        BigDecimal totalPrice = cart.getCartItems().stream()
                .map(cartItem -> cartItem.getProduct().getPrice().multiply(new BigDecimal(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, (sum, nextValue) -> sum.add(nextValue));
        return new CartResponse(
                cart.getId(),
                cart.getUser().getId(),
                cart.getCartItems().stream().map(cartItem -> CartItemMapper.toCartItemResponse(cartItem)).toList(),
                totalPrice
        );
    }

}
