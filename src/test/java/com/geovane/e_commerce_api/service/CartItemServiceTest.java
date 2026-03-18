package com.geovane.e_commerce_api.service;

import com.geovane.e_commerce_api.dto.request.CreateCartItemRequest;
import com.geovane.e_commerce_api.dto.response.CartItemResponse;
import com.geovane.e_commerce_api.exception.ForbiddenAccessException;
import com.geovane.e_commerce_api.exception.InsufficientStockException;
import com.geovane.e_commerce_api.exception.ResourceNotFoundException;
import com.geovane.e_commerce_api.model.*;
import com.geovane.e_commerce_api.repository.CartItemRepository;
import com.geovane.e_commerce_api.repository.CartRepository;
import com.geovane.e_commerce_api.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartItemService cartItemService;

    @Test
    void shouldReturnSavedCartItem() {
        CreateCartItemRequest request = new CreateCartItemRequest(1L, 1);
        User user = new User("User", "user@gmail.com", "password");
        Cart cart = new Cart(user);
        Category category = new Category("Category");
        Product product = new Product("Product", "Description", BigDecimal.valueOf(100), 100, category);
        CartItem cartItem = new CartItem(2, cart, product);

        Mockito.when(cartRepository.findByUserEmail("user@gmail.com")).thenReturn(Optional.of(cart));
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Mockito.when(cartItemRepository.findByCartIdAndProductId(any(), any())).thenReturn(Optional.empty());
        Mockito.when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        CartItemResponse returnedCartItem = cartItemService.save(request, "user@gmail.com");

        assertThat(returnedCartItem.productName()).isEqualTo(product.getName());
    }

    @Test
    void shouldReturnUpdatedCartItemWhenCartItemAlreadyExists() {
        CreateCartItemRequest request = new CreateCartItemRequest(1L, 1);
        User user = new User("User", "user@gmail.com", "password");
        Cart cart = new Cart(user);
        Category category = new Category("Category");
        Product product = new Product("Product", "Description", BigDecimal.valueOf(100), 100, category);
        CartItem cartItem = new CartItem(2, cart, product);

        Mockito.when(cartRepository.findByUserEmail("user@gmail.com")).thenReturn(Optional.of(cart));
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Mockito.when(cartItemRepository.findByCartIdAndProductId(any(), any())).thenReturn(Optional.of(cartItem));
        Mockito.when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        CartItemResponse returnedCartItem = cartItemService.save(request, user.getEmail());

        assertThat(cartItem.getQuantity()).isEqualTo(3);

    }

    @Test
    void shouldThrowExceptionWhenCartNotFound() {
        CreateCartItemRequest request = new CreateCartItemRequest(1L, 1);
        User user = new User("User", "user@gmail.com", "password");

        Mockito.when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartItemService.save(request, user.getEmail())).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        CreateCartItemRequest request = new CreateCartItemRequest(1L, 1);
        User user = new User("User", "user@gmail.com", "password");

        Mockito.when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(new Cart()));
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartItemService.save(request, user.getEmail())).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenInsufficientStockOnNewCartItem() {
        CreateCartItemRequest request = new CreateCartItemRequest(1L, 2);
        User user = new User("User", "user@gmail.com", "password");
        Category category = new Category("Category");
        Product product = new Product("Product", "Description", BigDecimal.valueOf(100), 1, category);

        Mockito.when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(new Cart()));
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Mockito.when(cartItemRepository.findByCartIdAndProductId(any(), any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartItemService.save(request, user.getEmail())).isInstanceOf(InsufficientStockException.class);
    }

    @Test
    void shouldThrowExceptionWhenInsufficientStockOnExistingCartItem() {
        CreateCartItemRequest request = new CreateCartItemRequest(1L, 1);
        User user = new User("User", "user@gmail.com", "password");
        Cart cart = new Cart(user);
        Category category = new Category("Category");
        Product product = new Product("Product", "Description", BigDecimal.valueOf(100), 1, category);
        CartItem cartItem = new CartItem(2, cart, product);

        Mockito.when(cartRepository.findByUserEmail(user.getEmail())).thenReturn(Optional.of(new Cart()));
        Mockito.when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Mockito.when(cartItemRepository.findByCartIdAndProductId(any(), any())).thenReturn(Optional.of(cartItem));

        assertThatThrownBy(() -> cartItemService.save(request, user.getEmail())).isInstanceOf(InsufficientStockException.class);
    }

    @Test
    void shouldDeleteCartItem() {
        User user = new User("User", "user@gmail.com", "password");
        Cart cart = new Cart(user);
        Category category = new Category("Category");
        Product product = new Product("Product", "Product description", BigDecimal.valueOf(10L), 1, category);
        CartItem cartItem = new CartItem(1, cart, product);

        Mockito.when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));

        cartItemService.delete(1L, user.getEmail());

        Mockito.verify(cartItemRepository).delete(cartItem);
    }

    @Test
    void shouldThrowExceptionWhenCartItemNotFound() {
        Mockito.when(cartItemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartItemService.delete(1L, "user@gmail.com")).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldThrowExceptionWhenCartUserEmailIsNotEqualsToAuthUserEmail() {
        User user = new User("User", "user@gmail.com", "password");
        Cart cart = new Cart(user);
        Category category = new Category("Category");
        Product product = new Product("Product", "Product description", BigDecimal.valueOf(10L), 1, category);
        CartItem cartItem = new CartItem(1, cart, product);

        Mockito.when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));

        cartItemService.delete(1L, user.getEmail());

        assertThatThrownBy(() -> cartItemService.delete(1L, "entity@gmail.com")).isInstanceOf(ForbiddenAccessException.class);
    }

}