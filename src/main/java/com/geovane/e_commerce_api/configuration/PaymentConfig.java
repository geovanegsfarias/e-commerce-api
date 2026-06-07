package com.geovane.e_commerce_api.configuration;

import com.stripe.Stripe;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(StripeConfigurationProperties.class)
public class PaymentConfig {
    private final StripeConfigurationProperties stripeProperties;

    public PaymentConfig(StripeConfigurationProperties stripeProperties) {
        this.stripeProperties = stripeProperties;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeProperties.secretKey();
    }
}
