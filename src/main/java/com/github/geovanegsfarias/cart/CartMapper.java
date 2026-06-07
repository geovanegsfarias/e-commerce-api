package com.github.geovanegsfarias.cart;

import com.github.geovanegsfarias.user.User;

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
