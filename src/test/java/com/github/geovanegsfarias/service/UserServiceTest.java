package com.github.geovanegsfarias.service;

import com.github.geovanegsfarias.dto.request.CreateUserRequest;
import com.github.geovanegsfarias.dto.response.UserResponse;
import com.github.geovanegsfarias.exception.ResourceAlreadyExistsException;
import com.github.geovanegsfarias.model.User;
import com.github.geovanegsfarias.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldReturnSavedUser() {
        CreateUserRequest request = new CreateUserRequest("User", "user@gmail.com", "password");
        User user = new User("User", "user@gmail.com", "password");

        Mockito.when(userRepository.existsByEmailIgnoreCase(request.email())).thenReturn(false);
        Mockito.when(encoder.encode(user.getPassword())).thenReturn("ajgi45agjki34");
        Mockito.when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse returnedUser = userService.save(request);

        assertThat(returnedUser.email()).isEqualTo(user.getEmail());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyIsInUse() {
        CreateUserRequest request = new CreateUserRequest("User", "user@gmail.com", "password");
        User user = new User("User", "user@gmail.com", "password");

        Mockito.when(userRepository.existsByEmailIgnoreCase(request.email())).thenReturn(true);

        assertThatThrownBy(() -> userService.save(request)).isInstanceOf(ResourceAlreadyExistsException.class);
    }

}