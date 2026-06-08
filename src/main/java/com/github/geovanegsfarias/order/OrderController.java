package com.github.geovanegsfarias.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/order")
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "Order")
@Slf4j
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper mapper;

    @Autowired
    public OrderController(OrderService orderService, OrderMapper mapper) {
        this.orderService = orderService;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(summary = "Get order history", description = "Retrieve a list of all orders of the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of orders.")
    @ApiResponse(responseCode = "401", description = "An error occurred while attempting to decode the Jwt: Malformed token.")
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred.")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(Authentication authentication, @ParameterObject Pageable pageable) {
        log.debug("Request received to list authenticated user's orders (page: {}, size: {})", pageable.getPageNumber(), pageable.getPageSize());

        var orders = orderService.findAll(authentication.getName(), pageable);

        var orderResponsePage = orders.map(order -> mapper.toOrderResponse(order));

        return ResponseEntity.ok(orderResponsePage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user order", description = "Retrieve a specific order of the authenticated user by ID.")
    @ApiResponse(responseCode = "200", description = "Order retrieved successfully.")
    @ApiResponse(responseCode = "401", description = "An error occurred while attempting to decode the Jwt: Malformed token.")
    @ApiResponse(responseCode = "404", description = "Order not found.")
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred.")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id, Authentication authentication) {
        log.debug("Request received to find order by id {}", id);

        var order = orderService.findByIdAndEmailOrThrowException(id, authentication.getName());

        var orderResponse = mapper.toOrderResponse(order);

        return ResponseEntity.ok(orderResponse);
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
        log.debug("Request received to create order for authenticated user");

        var savedOrder = orderService.save(authentication.getName());

        var orderResponse = mapper.toOrderResponse(savedOrder);

        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponse);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a order", description = "Deletes a pending order of the authenticated user.")
    @ApiResponse(responseCode = "204", description = "Order successfully deleted.")
    @ApiResponse(responseCode = "400", description = "This order cannot be deleted.")
    @ApiResponse(responseCode = "401", description = "An error occurred while attempting to decode the Jwt: Malformed token.")
    @ApiResponse(responseCode = "404", description = "Order not found.")
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred.")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id, Authentication authentication) {
        log.debug("Request received to delete order by id {}", id);

        orderService.delete(id, authentication.getName());

        return ResponseEntity.noContent().build();
    }

}
