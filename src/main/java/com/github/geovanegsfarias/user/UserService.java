package com.github.geovanegsfarias.user;

import com.github.geovanegsfarias.exception.ResourceAlreadyExistsException;
import com.github.geovanegsfarias.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final UserMapper mapper;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder encoder, UserMapper mapper) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.mapper = mapper;
    }

    public User findByEmailOrThrowException(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserResponse save(CreateUserRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new ResourceAlreadyExistsException("Email already registered.");
        }

        var user = mapper.toUser(request);

        user.setPassword(encoder.encode(user.getPassword()));
        return mapper.toUserResponse(userRepository.save(user));
    }

}