package com.geovane.e_commerce_api.repository;

import com.geovane.e_commerce_api.model.Cart;
import com.geovane.e_commerce_api.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CartRepositoryTest {

    @Autowired
    private CartRepository cartRepository;


    @Test
    void shouldReturnCartWhenCartExistsByUserEmail() {
        Cart cart = cartRepository.save(new Cart(new User("Arthur", "arthur@gmail.com", "password")));

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
