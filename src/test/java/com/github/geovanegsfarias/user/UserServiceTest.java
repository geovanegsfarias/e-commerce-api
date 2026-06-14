package com.github.geovanegsfarias.user;

import com.github.geovanegsfarias.commons.UserUtils;
import com.github.geovanegsfarias.exception.ResourceAlreadyExistsException;
import com.github.geovanegsfarias.exception.ResourceNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder encoder;
    @InjectMocks
    private UserService userService;
    private UserUtils userUtils;

    @BeforeEach
    void init() {
        userUtils = new UserUtils();
    }

    @Test
    @DisplayName("findByEmail returns a user with given email")
    @Order(1)
    void findByEmail_ReturnsUser_WhenSuccessful() {
        var expectedUser = userUtils.savedUser();

        BDDMockito.when(userRepository.findByEmailIgnoreCase(expectedUser.getEmail())).thenReturn(Optional.of(expectedUser));

        var user = userService.findByEmailOrThrowException(expectedUser.getEmail());

        Assertions.assertThat(user).isEqualTo(expectedUser);
    }

    @Test
    @DisplayName("findByEmail throws ResourceNotFoundException when user is not found")
    @Order(2)
    void findByEmail_ThrowsResourceNotFoundException_WhenUserNotFound() {
        var expectedUser = userUtils.savedUser();

        BDDMockito.when(userRepository.findByEmailIgnoreCase(expectedUser.getEmail())).thenReturn(Optional.empty());

        Assertions.assertThatException()
                .isThrownBy(() -> userService.findByEmailOrThrowException(expectedUser.getEmail()))
                .isInstanceOf(ResourceNotFoundException.class)
                .withMessage("User not found");
    }

    @Test
    @DisplayName("save creates a user")
    @Order(3)
    void save_CreatesUser_WhenSuccessful() {
        var userToSave = userUtils.newUserToSave();

        var encodedPassword = "encoded-password";

        BDDMockito.when(userRepository.existsByEmailIgnoreCase(userToSave.getEmail())).thenReturn(false);
        BDDMockito.when(encoder.encode(userToSave.getPassword())).thenReturn(encodedPassword);
        BDDMockito.when(userRepository.save(userToSave)).thenReturn(userToSave);

        var savedUser = userService.save(userToSave);

        Assertions.assertThat(savedUser).isEqualTo(userToSave);
        Assertions.assertThat(savedUser.getPassword()).isEqualTo(encodedPassword);
    }

    @Test
    @DisplayName("save throws ResourceAlreadyExistsException when user email is already registered")
    @Order(4)
    void save_ThrowsResourceAlreadyExistsException_WhenUserEmailIsAlreadyRegistered() {
        var userToSave = userUtils.newUserToSave();

        BDDMockito.when(userRepository.existsByEmailIgnoreCase(userToSave.getEmail())).thenReturn(true);

        Assertions.assertThatException()
                .isThrownBy(() -> userService.save(userToSave))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .withMessage("Email already registered");
    }

}