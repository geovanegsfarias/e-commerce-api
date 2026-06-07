package com.geovane.e_commerce_api.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.stripe")
public record StripeConfigurationProperties(
        String secretKey,
        String webhookSecret,
        String successUrl,
        String cancelUrl) {
}
