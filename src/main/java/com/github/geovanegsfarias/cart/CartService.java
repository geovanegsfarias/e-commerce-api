package com.github.geovanegsfarias.cart;

import com.github.geovanegsfarias.exception.ResourceNotFoundException;
import com.github.geovanegsfarias.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserService userService;

    @Autowired
    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, UserService userService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
    }

    public Cart findByEmailOrThrowException(String email) {
        return cartRepository.findByUserEmail(email).orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    }

    public Cart save(String userEmail) {
        return cartRepository.findByUserEmail(userEmail).orElseGet(() -> createCart(userEmail));
    }

    @Transactional
    public void delete(String userEmail) {
        var cartToDelete = findByEmailOrThrowException(userEmail);
        cartItemRepository.deleteAllByCartId(cartToDelete.getId());
    }

    private Cart createCart(String userEmail) {
        var user = userService.findByEmailOrThrowException(userEmail);
        var cart = new Cart(user);
        return cartRepository.save(cart);
    }

}
