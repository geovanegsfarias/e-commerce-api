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

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public User findByEmailOrThrowException(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User save(User userToSave) {
        assertEmailIsAvailable(userToSave.getEmail());
        userToSave.setPassword(encoder.encode(userToSave.getPassword()));
        return userRepository.save(userToSave);
    }

    private void assertEmailIsAvailable(String email) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResourceAlreadyExistsException("Email already registered");
        }
    }

}