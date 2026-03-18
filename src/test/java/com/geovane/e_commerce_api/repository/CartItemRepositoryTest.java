package com.geovane.e_commerce_api.repository;

import com.geovane.e_commerce_api.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void shouldReturnCartItemWhenCartExistsByIdAndProductId() {
        User user = userRepository.save(new User("user", "user@gmail.com", "password"));
        Cart cart = cartRepository.save(new Cart(user));
        Category category = categoryRepository.save(new Category("Console"));
        Product product = productRepository.save(new Product("Xbox One", "Xbox One description.", BigDecimal.TEN, 1, category));
        CartItem cartItem = cartItemRepository.save(new CartItem(1, cart, product));

        Optional<CartItem> cartItemOptional = cartItemRepository.findByCartIdAndProductId(cartItem.getCart().getId(), cartItem.getProduct().getId());

        assertThat(cartItemOptional).isPresent();
        assertThat(cartItemOptional.get().getId()).isEqualTo(cartItem.getId());
    }

    @Test
    void shouldReturnEmptyWhenCartNotExistsByIdAndProductId() {
        Optional<CartItem> cartItemOptional = cartItemRepository.findByCartIdAndProductId(4L, 3L);

        assertThat(cartItemOptional).isEmpty();
    }

    @Test
    void shouldDeleteAllItemsByCartId() {
        User user = userRepository.save(new User("user", "user@gmail.com", "password"));
        Cart cart = cartRepository.save(new Cart(user));
        Category category = categoryRepository.save(new Category("Console"));
        Product product = productRepository.save(new Product("Xbox One", "Xbox One description.", BigDecimal.TEN, 1, category));
        CartItem cartItem = cartItemRepository.save(new CartItem(1, cart, product));

        cartItemRepository.deleteAllByCartId(cartItem.getCart().getId());
        Optional<CartItem> cartItemOptional = cartItemRepository.findById(cartItem.getId());

        assertThat(cartItemOptional).isEmpty();
    }

}