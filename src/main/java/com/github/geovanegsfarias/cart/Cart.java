package com.github.geovanegsfarias.cart;

import com.github.geovanegsfarias.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cart")
@Getter
@Setter
@AllArgsConstructor
@Builder
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "cart", orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    public Cart() {
    }

    public Cart(User user) {
        this.user = user;
    }

}