package com.geovane.e_commerce_api.controller;

import com.geovane.e_commerce_api.dto.response.OrderResponse;
import com.geovane.e_commerce_api.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Order")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @Operation(summary = "Get order history", description = "Retrieve a list of all orders of the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of orders.")
    @ApiResponse(responseCode = "401", description = "An error occurred while attempting to decode the Jwt: Malformed token.")
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred.")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(Authentication authentication, @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(orderService.getAll(authentication.getName(), pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user order", description = "Retrieve a specific order of the authenticated user by ID.")
    @ApiResponse(responseCode = "200", description = "Order retrieved successfully.")
    @ApiResponse(responseCode = "401", description = "An error occurred while attempting to decode the Jwt: Malformed token.")
    @ApiResponse(responseCode = "404", description = "Order not found.")
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred.")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(orderService.getByIdAndEmail(id, authentication.getName()));
    }

    @PostMapping
    @Operation(summary = "Creates a new order", description = "Create a new order for the authenticated user.")
    @ApiResponse(responseCode = "201", description = "Order successfully created.")
    @ApiResponse(responseCode = "400", description = "Insufficient Stock.")
    @ApiResponse(responseCode = "401", description = "An error occurred while attempting to decode the Jwt: Malformed token.")
    @ApiResponse(responseCode = "404", description = "Your cart is empty.")
    @ApiResponse(responseCode = "404", description = "User not found.")
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred.")
    public ResponseEntity<OrderResponse> saveOrder(Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.save(authentication.getName()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a order", description = "Deletes a pending order of the authenticated user.")
    @ApiResponse(responseCode = "204", description = "Order successfully deleted.")
    @ApiResponse(responseCode = "400", description = "This order cannot be deleted.")
    @ApiResponse(responseCode = "401", description = "An error occurred while attempting to decode the Jwt: Malformed token.")
    @ApiResponse(responseCode = "404", description = "Order not found.")
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred.")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id, Authentication authentication) {
        orderService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

}