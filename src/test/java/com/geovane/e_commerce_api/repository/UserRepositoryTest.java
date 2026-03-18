package com.geovane.e_commerce_api.repository;

import com.geovane.e_commerce_api.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;


@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldReturnUserWhenEmailExists() {
        userRepository.save(new User("Marcos", "marcos@gmail.com", "password"));

        Optional<User> user = userRepository.findByEmail("marcos@gmail.com");

        assertThat(user).isPresent();
        assertThat(user.get().getEmail()).isEqualTo("marcos@gmail.com");
    }

    @Test
    void shouldReturnEmptyWhenEmailNotFound() {
        Optional<User> user = userRepository.findByEmail("donatello@gmail.com");

        assertThat(user).isEmpty();
    }

    @Test
    void shouldReturnTrueWhenEmailExistsIgnoringCase() {
        userRepository.save(new User("José", "jose@gmail.com", "password"));

        boolean exists = userRepository.existsByEmailIgnoreCase("JOSE@gmail.com");

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenEmailNotExistsIgnoringCase() {
        Boolean exists = userRepository.existsByEmailIgnoreCase("void@gmail.com");

        assertThat(exists).isFalse();
    }

}