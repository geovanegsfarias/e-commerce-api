package com.geovane.e_commerce_api.controller;

import com.geovane.e_commerce_api.dto.request.CreateCartItemRequest;
import com.geovane.e_commerce_api.dto.response.CartItemResponse;
import com.geovane.e_commerce_api.dto.response.CartResponse;
import com.geovane.e_commerce_api.service.CartItemService;
import com.geovane.e_commerce_api.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Cart")
public class CartController {
    private final CartService cartService;
    private final CartItemService cartItemService;

    @Autowired
    public CartController(CartService cartService, CartItemService cartItemService) {
        this.cartService = cartService;
        this.cartItemService = cartItemService;
    }

    @GetMapping
    @Operation(summary = "Get user cart", description = "Returns the authenticated user's shopping cart.")
    @ApiResponse(responseCode = "200", description = "Cart retrieved successfully.")
    @ApiResponse(responseCode = "401", description = "An error occurred while attempting to decode the Jwt: Malformed token.")
    @ApiResponse(responseCode = "404", description = "Cart not found.")
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred.")
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        return ResponseEntity.ok(cartService.getByEmail(authentication.getName()));
    }

    @PostMapping
    @Operation(summary = "Add a new cart", description = "Creates a shopping cart for the authenticated user.")
    @ApiResponse(responseCode = "201", description = "Cart successfully created.")
    @ApiResponse(responseCode = "401", description = "An error occurred while attempting to decode the Jwt: Malformed token.")
    @ApiResponse(responseCode = "404", description = "User not found.")
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred.")
    public ResponseEntity<CartResponse> saveCart(Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.save(authentication.getName()));
    }

    @DeleteMapping
    @Operation(summary = "Delete a cart", description = "Clears all items from the authenticated user's shopping cart.")
    @ApiResponse(responseCode = "204", description = "Cart successfully deleted.")
    @ApiResponse(responseCode = "401", description = "An error occurred while attempting to decode the Jwt: Malformed token.")
    @ApiResponse(responseCode = "404", description = "Cart not found.")
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred.")
    public ResponseEntity<Void> deleteCart(Authentication authentication) {
        cartService.delete(authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart", description = "Adds a product to the authenticated user's shopping cart.")
    @ApiResponse(responseCode = "201", description = "Cart item successfully created.")
    @ApiResponse(responseCode = "400", description = "Insufficient Stock.")
    @ApiResponse(responseCode = "400", description = "Invalid request data.")
    @ApiResponse(responseCode = "401", description = "An error occurred while attempting to decode the Jwt: Malformed token.")
    @ApiResponse(responseCode = "404", description = "Cart not found.")
    @ApiResponse(responseCode = "404", description = "Product not found.")
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred.")
    public ResponseEntity<CartItemResponse> saveCartItem(@RequestBody @Valid CreateCartItemRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemService.save(request, authentication.getName()));
    }

    @DeleteMapping("/items/{id}")
    @Operation(summary = "Remove item from cart", description = "Removes a specific item from the authenticated user's cart by ID.")
    @ApiResponse(responseCode = "204", description = "Cart item successfully deleted.")
    @ApiResponse(responseCode = "401", description = "An error occurred while attempting to decode the Jwt: Malformed token.")
    @ApiResponse(responseCode = "403", description = "Access denied.")
    @ApiResponse(responseCode = "404", description = "Cart item not found.")
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred.")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long id, Authentication authentication) {
        cartItemService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

}
