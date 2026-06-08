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
    private final CartMapper mapper;

    @Autowired
    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, UserService userService, CartMapper mapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
        this.mapper = mapper;
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
        var cart = mapper.toCart(user);
        return cartRepository.save(cart);
    }

}
