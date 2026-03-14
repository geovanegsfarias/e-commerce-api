package com.geovane.e_commerce_api.repository;

import com.geovane.e_commerce_api.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{

    Page<Order> findAllByUserEmail(String email, Pageable pageable);
    Optional<Order> findByIdAndUserEmail(Long id, String email);
}