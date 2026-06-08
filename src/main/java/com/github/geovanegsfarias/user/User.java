package com.github.geovanegsfarias.user;

import com.github.geovanegsfarias.cart.Cart;
import com.github.geovanegsfarias.order.Order;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String password;
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @OneToOne(mappedBy = "user", orphanRemoval = true)
    private Cart cart;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

    public User() {
    }

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = UserRole.ROLE_USER;
    }

}