package com.ievolve.jwt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ievolve.jwt.model.Product;

public interface ProductRepo extends JpaRepository<Product, Integer> {

    List<Product> findByProductNameContainingIgnoreCaseOrCategoryCategoryNameContainingIgnoreCase(String productName, String categoryName);

    List<Product> findBySellerUserId(Integer sellerId);

    Optional<Product> findBySellerUserIdAndProductId(Integer sellerId, Integer productId);
}
