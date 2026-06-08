package com.github.geovanegsfarias.cart;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/cart")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Cart")
@Slf4j
public class CartController {
    private final CartService cartService;
    private final CartItemService cartItemService;
    private final CartMapper cartMapper;
    private final CartItemMapper cartItemMapper;

    @Autowired
    public CartController(CartService cartService, CartItemService cartItemService, CartMapper cartMapper, CartItemMapper cartItemMapper) {
        this.cartService = cartService;
        this.cartItemService = cartItemService;
        this.cartMapper = cartMapper;
        this.cartItemMapper = cartItemMapper;
    }

    @GetMapping
    @Operation(summary = "Get cart")
    @ApiResponse(responseCode = "200", description = "Cart returned")
    @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Cart not found", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        log.debug("Request received to find authenticated user's cart");

        var cart = cartService.findByEmailOrThrowException(authentication.getName());

        var cartResponse = cartMapper.toCartResponse(cart);

        return ResponseEntity.ok(cartResponse);
    }

    @PostMapping
    @Operation(summary = "Create cart")
    @ApiResponse(responseCode = "201", description = "Cart created")
    @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<CartResponse> saveCart(Authentication authentication) {
        log.debug("Request received to save authenticated user's cart");

        var savedCart = cartService.save(authentication.getName());

        var cartResponse = cartMapper.toCartResponse(savedCart);

        return ResponseEntity.status(HttpStatus.CREATED).body(cartResponse);
    }

    @DeleteMapping
    @Operation(summary = "Clear cart")
    @ApiResponse(responseCode = "204", description = "Cart cleared")
    @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Cart not found", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Void> deleteCart(Authentication authentication) {
        log.debug("Request received to clear authenticated user's cart");

        cartService.delete(authentication.getName());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/items")
    @Operation(summary = "Add item to cart")
    @ApiResponse(responseCode = "201", description = "Item added")
    @ApiResponse(responseCode = "400", description = "Invalid data or insufficient stock", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Cart or product not found", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<CartItemResponse> saveCartItem(@RequestBody @Valid CreateCartItemRequest request, Authentication authentication) {
        log.debug("Request received to add product {} to cart with quantity {}", request.productId(), request.quantity());

        var cartItemToSave = cartItemMapper.toCartItem(request);

        var savedCartItem = cartItemService.save(cartItemToSave, request.productId(), authentication.getName());

        var cartItemResponse = cartItemMapper.toCartItemResponse(savedCartItem);

        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemResponse);
    }

    @DeleteMapping("/items/{id}")
    @Operation(summary = "Remove item from cart")
    @ApiResponse(responseCode = "204", description = "Item removed")
    @ApiResponse(responseCode = "401", description = "Authentication required", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "403", description = "Insufficient permissions", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "404", description = "Cart item not found", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE, schema = @Schema(implementation = ProblemDetail.class)))
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long id, Authentication authentication) {
        log.debug("Request received to delete cart item by id {}", id);

        cartItemService.delete(id, authentication.getName());

        return ResponseEntity.noContent().build();
    }

}
