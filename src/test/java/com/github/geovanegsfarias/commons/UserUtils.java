package com.github.geovanegsfarias.commons;

import com.github.geovanegsfarias.user.User;
import com.github.geovanegsfarias.user.UserRole;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {

    public User newUserToSave() {
        return User.builder()
                .name("User")
                .email("user@gmail.com")
                .password("{bcrypt}$2a$10$zDReSHJiP0DIfAucmf0RnO5RLKjqEeaEKzh3PSueyMG54ku95EfZu")
                .role(UserRole.ROLE_USER)
                .build();
    }

    public User savedUser() {
        return User.builder()
                .id(1L)
                .name("User")
                .email("user@gmail.com")
                .password("{bcrypt}$2a$10$zDReSHJiP0DIfAucmf0RnO5RLKjqEeaEKzh3PSueyMG54ku95EfZu")
                .role(UserRole.ROLE_USER)
                .build();
    }
}