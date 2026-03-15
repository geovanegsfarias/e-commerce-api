package com.geovane.e_commerce_api.configuration;

import com.geovane.e_commerce_api.model.User;
import com.geovane.e_commerce_api.model.UserRole;
import com.geovane.e_commerce_api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedUsers(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            if (userRepository.findByEmail("admin@gmail.com").isEmpty()) {
                User admin = new User("Admin", "admin@gmail.com", encoder.encode("admin123"));
                admin.setRole(UserRole.ROLE_ADMIN);
                userRepository.save(admin);
            }
        };
    }

}
