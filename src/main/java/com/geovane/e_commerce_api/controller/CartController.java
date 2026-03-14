package com.geovane.e_commerce_api.controller;

import com.geovane.e_commerce_api.dto.request.CreateCartItemRequest;
import com.geovane.e_commerce_api.dto.response.CartItemResponse;
import com.geovane.e_commerce_api.dto.response.CartResponse;
import com.geovane.e_commerce_api.service.CartItemService;
import com.geovane.e_commerce_api.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;
    private final CartItemService cartItemService;

    @Autowired
    public CartController(CartService cartService, CartItemService cartItemService) {
        this.cartService = cartService;
        this.cartItemService = cartItemService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        return ResponseEntity.ok(cartService.getByEmail(authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<CartResponse> saveCart(Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.save(authentication.getName()));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteCart(Authentication authentication) {
        cartService.delete(authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/items")
    public ResponseEntity<CartItemResponse> saveCartItem(@RequestBody @Valid CreateCartItemRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemService.save(request, authentication.getName()));
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long id, Authentication authentication) {
        cartItemService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

}
