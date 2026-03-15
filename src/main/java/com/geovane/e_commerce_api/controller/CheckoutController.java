package com.geovane.e_commerce_api.controller;

import com.geovane.e_commerce_api.dto.request.StripeRequest;
import com.geovane.e_commerce_api.dto.response.StripeResponse;
import com.geovane.e_commerce_api.service.StripeService;
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
public class CheckoutController {
    private final StripeService stripeService;

    @Autowired
    public CheckoutController(StripeService stripeService) {
        this.stripeService = stripeService;
    }

    @PostMapping
    public ResponseEntity<StripeResponse> checkoutOrder(@RequestBody @Valid StripeRequest request, Authentication authentication) {
        StripeResponse response = stripeService.checkoutOrder(request, authentication.getName());
        return ResponseEntity.ok().body(response);
    }
}
