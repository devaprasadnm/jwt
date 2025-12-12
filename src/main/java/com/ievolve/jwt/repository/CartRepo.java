package com.ievolve.jwt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ievolve.jwt.model.Cart;

public interface CartRepo extends JpaRepository<Cart, Integer> {
    Optional<Cart> findByUserUserId(Integer userId);

}
