package com.geovane.e_commerce_api.repository;

import com.geovane.e_commerce_api.model.Order;
import com.geovane.e_commerce_api.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldReturnOrderPagesWhenUserEmailExists() {
        User user = userRepository.save(new User("user", "user@gmail.com", "password"));
        orderRepository.save(new Order(BigDecimal.TEN, user, new ArrayList<>()));

        Page<Order> ordersPage = orderRepository.findAllByUserEmail(user.getEmail(), Pageable.unpaged());

        assertThat(ordersPage).isNotEmpty();
    }

    @Test
    void shouldReturnEmptyPageWhenUserHasNoOrder() {
        Page<Order> ordersPage = orderRepository.findAllByUserEmail("user@gmail.com", Pageable.unpaged());

        assertThat(ordersPage).isEmpty();
    }

    @Test
    void shouldReturnOrderWhenIdAndUserEmailExists() {
        User user = userRepository.save(new User("user", "user@gmail.com", "password"));
        Order order = orderRepository.save(new Order(BigDecimal.TEN, user, new ArrayList<>()));

        Optional<Order> orderOptional = orderRepository.findByIdAndUserEmail(order.getId(), user.getEmail());

        assertThat(orderOptional).isPresent();
        assertThat(orderOptional.get()).isEqualTo(order);
    }

    @Test
    void shouldReturnEmptyWhenOrderNotFoundByIdAndUserEmail() {
        Optional<Order> orderOptional = orderRepository.findByIdAndUserEmail(5L, "void@gmail.com");

        assertThat(orderOptional).isEmpty();
    }

}