package com.ievolve.jwt.controller;
import com.ievolve.jwt.dto.JwtResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ievolve.jwt.dto.AuthRequest;
import com.ievolve.jwt.service.JwtService;

@RequestMapping("/api/public")
public class LoginController {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateAndgetToken(@RequestBody AuthRequest authRequest){

        try{
           Authentication auth =  authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(),authRequest.getPassword())
            );
           String token = jwtService.generateToken(authRequest.getUsername());
           return  ResponseEntity.ok(new JwtResponse(token,200));

        } catch (Exception e) {
            throw new RuntimeException(e);

        }
//        return ResponseEntity.status(401).body("login failed");

    }
    
}
