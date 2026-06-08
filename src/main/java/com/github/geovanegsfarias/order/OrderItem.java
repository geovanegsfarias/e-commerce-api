package com.github.geovanegsfarias.order;

import com.github.geovanegsfarias.product.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "order_item")
@Getter
@Setter
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal price;
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    public OrderItem() {
    }

    public OrderItem(BigDecimal price, int quantity, Order order, Product product) {
        this.price = price;
        this.quantity = quantity;
        this.order = order;
        this.product = product;
    }

}