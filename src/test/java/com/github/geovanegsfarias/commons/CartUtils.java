package com.github.geovanegsfarias.commons;

import com.github.geovanegsfarias.cart.Cart;
import com.github.geovanegsfarias.cart.CartItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartUtils {
    private final UserUtils userUtils;
    private final ProductUtils productUtils;

    public CartUtils(UserUtils userUtils, ProductUtils productUtils) {
        this.userUtils = userUtils;
        this.productUtils = productUtils;
    }

    public Cart newCartToSave() {
        var user = userUtils.savedUser();

        return Cart.builder()
                .user(user)
                .build();
    }

    public Cart savedCart() {
        var user = userUtils.savedUser();

        return Cart.builder()
                .id(1L)
                .user(user)
                .build();
    }

    public CartItem newCartItemToSave() {
        return CartItem.builder()
                .quantity(2)
                .build();
    }

    public CartItem savedCartItem() {
        var cart = savedCart();
        var product = productUtils.savedProduct();

        return CartItem.builder()
                .id(1L)
                .quantity(5)
                .cart(cart)
                .product(product)
                .build();
    }

    public Cart savedCartWithItems() {
        var user = userUtils.savedUser();
        var product = productUtils.savedProduct();

        var cart = Cart.builder()
                .id(1L)
                .user(user)
                .build();

        var cartItem = CartItem.builder()
                .id(1L)
                .quantity(5)
                .cart(cart)
                .product(product)
                .build();

        cart.setCartItems(List.of(cartItem));

        return cart;
    }

}
