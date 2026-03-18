package com.geovane.e_commerce_api.service;

import com.geovane.e_commerce_api.dto.response.CartResponse;
import com.geovane.e_commerce_api.exception.ResourceNotFoundException;
import com.geovane.e_commerce_api.model.Cart;
import com.geovane.e_commerce_api.model.User;
import com.geovane.e_commerce_api.repository.CartItemRepository;
import com.geovane.e_commerce_api.repository.CartRepository;
import com.geovane.e_commerce_api.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    void shouldReturnCartByEmail() {
        User user = new User("User", "user@gmail.com", "password");
        Cart cart = new Cart(user);

        Mockito.when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(cart));

        CartResponse returnedCart = cartService.getByEmail(user.getEmail());

        assertThat(returnedCart.items()).isEmpty();
    }

    @Test
    void shouldThrowExceptionWhenCartNotFound() {
        Mockito.when(cartRepository.findByUserEmail("user@gmail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.getByEmail("user@gmail.com")).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldReturnSavedCart() {
        User user = new User("User", "user@gmail.com", "password");
        Cart cart = new Cart(user);

        Mockito.when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        Mockito.when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartResponse returnedCart = cartService.save(user.getEmail());

        assertThat(returnedCart).isNotNull();
    }

    @Test
    void shouldReturnExistingCartWhenCartAlreadyExists() {
        User user = new User("User", "user@gmail.com", "password");
        Cart cart = new Cart(user);

        Mockito.when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(cart));

        CartResponse returnedCart = cartService.save(user.getEmail());

        assertThat(returnedCart).isNotNull();
        Mockito.verify(userRepository, Mockito.never()).findByEmail(any());
        Mockito.verify(cartRepository, Mockito.never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        Mockito.when(cartRepository.findByUserEmail("user@gmail.com")).thenReturn(Optional.empty());
        Mockito.when(userRepository.findByEmail("user@gmail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.save("user@gmail.com")).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldDeleteAllCartItemsOnCart() {
        Cart cart = new Cart(new User("User", "user@gmail.com", "password"));

        Mockito.when(cartRepository.findByUserEmail(cart.getUser().getEmail())).thenReturn(Optional.of(cart));

        cartService.delete(cart.getUser().getEmail());

        Mockito.verify(cartItemRepository).deleteAllByCartId(any());
    }

    @Test
    void shouldThrowExceptionWhenCartNotFoundOnDelete() {
        Mockito.when(cartRepository.findByUserEmail("user@gmail.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.delete("user@gmail.com")).isInstanceOf(ResourceNotFoundException.class);
    }

}
