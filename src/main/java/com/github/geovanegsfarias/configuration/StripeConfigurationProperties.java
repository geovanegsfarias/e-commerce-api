package com.github.geovanegsfarias.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.stripe")
public record StripeConfigurationProperties(
        String secretKey,
        String webhookSecret) {
}
