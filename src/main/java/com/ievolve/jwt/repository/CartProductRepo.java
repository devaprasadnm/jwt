package com.ievolve.jwt.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ievolve.jwt.model.CartProduct;

import jakarta.transaction.Transactional;

public interface CartProductRepo extends JpaRepository<CartProduct, Integer> {
    Optional<CartProduct> findByCartUserUserIdAndProductProductId(Integer userId, Integer productId);

    @Transactional
    void deleteByCartUserUserIdAndProductProductId(Integer userId, Integer productId);
}
