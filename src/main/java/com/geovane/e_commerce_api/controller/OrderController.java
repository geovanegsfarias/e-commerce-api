package com.geovane.e_commerce_api.controller;

import com.geovane.e_commerce_api.dto.response.OrderResponse;
import com.geovane.e_commerce_api.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getAllOrders(Authentication authentication, Pageable pageable) {
        return ResponseEntity.ok(orderService.getAll(authentication.getName(), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(orderService.getByIdAndEmail(id, authentication.getName()));
    }

    @PostMapping
    public ResponseEntity<OrderResponse> saveOrder(Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.save(authentication.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id, Authentication authentication) {
        orderService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

}