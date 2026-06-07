package com.geovane.e_commerce_api.configuration;

import com.geovane.e_commerce_api.model.User;
import com.geovane.e_commerce_api.model.UserRole;
import com.geovane.e_commerce_api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableConfigurationProperties(DevConfigurationProperties.class)
@Profile("dev")
public class DevDataInitializer {
    private final DevConfigurationProperties configurationProperties;

    public DevDataInitializer(DevConfigurationProperties configurationProperties) {
        this.configurationProperties = configurationProperties;
    }

    @Bean
    public CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            if (!userRepository.existsByEmailIgnoreCase(configurationProperties.email())) {
                User admin = new User("Admin", configurationProperties.email(), encoder.encode(configurationProperties.password()));
                admin.setRole(UserRole.ROLE_ADMIN);
                userRepository.save(admin);
            }
        };
    }

}
