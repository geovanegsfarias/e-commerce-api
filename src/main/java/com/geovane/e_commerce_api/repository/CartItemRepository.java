package com.geovane.e_commerce_api.repository;

import com.geovane.e_commerce_api.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);
    void deleteAllByCartId(Long cartId);
}