package com.github.geovanegsfarias.cart;

import com.github.geovanegsfarias.exception.ResourceNotFoundException;
import com.github.geovanegsfarias.user.User;
import com.github.geovanegsfarias.user.UserRepository;
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
