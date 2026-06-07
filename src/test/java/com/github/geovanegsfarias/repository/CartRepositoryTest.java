package com.github.geovanegsfarias.repository;

import com.github.geovanegsfarias.cart.Cart;
import com.github.geovanegsfarias.cart.CartRepository;
import com.github.geovanegsfarias.user.User;
import com.github.geovanegsfarias.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldReturnCartWhenCartExistsByUserEmail() {
        User user = new User("Arthur", "arthur@gmail.com", "password");
        userRepository.save(user);
        cartRepository.save(new Cart(user));

        Optional<Cart> cartOptional = cartRepository.findByUserEmail("arthur@gmail.com");

        assertThat(cartOptional).isPresent();
        assertThat(cartOptional.get().getUser().getEmail()).isEqualTo("arthur@gmail.com");
    }

    @Test
    void shouldReturnEmptyWhenCartNotExistsByUserEmail() {
        Optional<Cart> cartOptional = cartRepository.findByUserEmail("void@gmail.com");

        assertThat(cartOptional).isEmpty();
    }

}
