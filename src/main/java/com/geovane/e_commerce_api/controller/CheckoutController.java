package com.geovane.e_commerce_api.controller;

import com.geovane.e_commerce_api.dto.request.StripeRequest;
import com.geovane.e_commerce_api.dto.response.StripeResponse;
import com.geovane.e_commerce_api.service.StripeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkout")
@Tag(name = "Checkout")
public class CheckoutController {
    private final StripeService stripeService;

    @Autowired
    public CheckoutController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping
    @Operation(summary = "Generates a payment link.", description = "Creates a session in Stripe and returns a URL for checkout.")
    @ApiResponse(responseCode = "200", description = "payment link successfully created.")
    @ApiResponse(responseCode = "400", description = "This order has already been completed.")
    @ApiResponse(responseCode = "400", description = "Insufficient stock.")
    @ApiResponse(responseCode = "400", description = "Error occurring while creating a payment session.")
    @ApiResponse(responseCode = "400", description = "Signature error.")
    @ApiResponse(responseCode = "400", description = "Webhook error.")
    @ApiResponse(responseCode = "404", description = "Order not found.")
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred.")
    public ResponseEntity<StripeResponse> checkoutOrder(@RequestBody @Valid StripeRequest request, Authentication authentication) {
        StripeResponse response = stripeService.checkoutOrder(request, authentication.getName());
        return ResponseEntity.ok().body(response);
    }
}
