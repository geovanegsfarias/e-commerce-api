package com.github.geovanegsfarias.service;

import com.github.geovanegsfarias.cart.CartService;
import com.github.geovanegsfarias.cart.CartResponse;
import com.github.geovanegsfarias.exception.ResourceNotFoundException;
import com.github.geovanegsfarias.cart.Cart;
import com.github.geovanegsfarias.user.User;
import com.github.geovanegsfarias.cart.CartItemRepository;
import com.github.geovanegsfarias.cart.CartRepository;
import com.github.geovanegsfarias.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
