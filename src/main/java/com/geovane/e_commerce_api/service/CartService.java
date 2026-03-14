package com.geovane.e_commerce_api.service;

import com.geovane.e_commerce_api.dto.response.CartResponse;
import com.geovane.e_commerce_api.exception.ResourceNotFoundException;
import com.geovane.e_commerce_api.mapper.CartMapper;
import com.geovane.e_commerce_api.model.Cart;
import com.geovane.e_commerce_api.model.User;
import com.geovane.e_commerce_api.repository.CartItemRepository;
import com.geovane.e_commerce_api.repository.CartRepository;
import com.geovane.e_commerce_api.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;

    @Autowired
    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
    }

    public CartResponse getByEmail(String email) {
        return CartMapper.toCartResponse(
                cartRepository.findByUserEmail(email).orElseThrow(() -> new ResourceNotFoundException("Cart not found."))
        );
    }

    public CartResponse save(String email) {
        Optional<Cart> optCart = cartRepository.findByUserEmail(email);

        if (optCart.isPresent()) {
            return CartMapper.toCartResponse(optCart.get());
        }

        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found."));
        return CartMapper.toCartResponse(
                cartRepository.save(CartMapper.toCart(user))
        );
    }

    @Transactional
    public void delete(String email) {
        Cart cart = cartRepository.findByUserEmail(email).orElseThrow(() -> new ResourceNotFoundException("Cart not found."));
        cartItemRepository.deleteAllByCartId(cart.getId());
    }

}
