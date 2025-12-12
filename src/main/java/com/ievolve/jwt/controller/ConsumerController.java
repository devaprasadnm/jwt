package com.ievolve.jwt.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ievolve.jwt.model.Product;
import com.ievolve.jwt.repository.CartProductRepo;
import com.ievolve.jwt.repository.CartRepo;
import com.ievolve.jwt.repository.ProductRepo;
import com.ievolve.jwt.repository.UserInfoRepository;

@RequestMapping("/api/auth/consumer")
public class ConsumerController {
    
    ProductRepo productRepo;

    CartRepo cartRepo;

    CartProductRepo cartProductRepo;

    UserInfoRepository userInfoRepository;

    @GetMapping("/cart")
    public ResponseEntity<Object> getCart(Principal principal){
        return null;
    }

    @PostMapping("/cart")
    public ResponseEntity<Object> addToCart(Principal principal, Product product){
        return null;
    }
    
}
