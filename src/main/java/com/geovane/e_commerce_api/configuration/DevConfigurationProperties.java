package com.geovane.e_commerce_api.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.admin")
public record DevConfigurationProperties(String email, String password) {
}
