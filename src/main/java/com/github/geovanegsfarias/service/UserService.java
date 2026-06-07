package com.github.geovanegsfarias.service;

import com.github.geovanegsfarias.dto.request.CreateUserRequest;
import com.github.geovanegsfarias.dto.response.UserResponse;
import com.github.geovanegsfarias.exception.ResourceAlreadyExistsException;
import com.github.geovanegsfarias.mapper.UserMapper;
import com.github.geovanegsfarias.model.User;
import com.github.geovanegsfarias.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public UserResponse save(CreateUserRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ResourceAlreadyExistsException("Email already registered.");
        }

        User user = UserMapper.toUser(request);

        user.setPassword(encoder.encode(user.getPassword()));
        return UserMapper.toUserResponse(userRepository.save(user));
    }

}