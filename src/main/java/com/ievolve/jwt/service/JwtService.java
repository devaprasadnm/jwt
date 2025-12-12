package com.ievolve.jwt.service;

import java.security.Key;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtService {
    public static final String SECRET = "3346782465DJ87389NVKDNV9I3DJCVSJKABC84592475982498574";
    public static final long JWT_TOKEN_VALIDITY = 900000; // 15 minutes

    public String extractUsername(String token) {
        return  extractAllClaims(token).getSubject();
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        // Implementation to extract specific claim from token
        return null;
    }

    private Claims extractAllClaims(String token) {
        // Implementation to extract all claims from token
        return Jwts.parserBuilder().
                setSigningKey(getSignInKey()).
                build().
                parseClaimsJws(token).
                getBody();
    }

    private Boolean isTokenExpired(String token) {
        return  extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails){
       return !isTokenExpired(token) && userDetails.getUsername().equals(extractUsername(token));
    }

    public String generateToken(String username) {
        return Jwts.builder().
                setSubject(username).
                setIssuedAt(new Date(System.currentTimeMillis())).
                setExpiration(new Date(System.currentTimeMillis()+JWT_TOKEN_VALIDITY)).
                signWith(getSignInKey(), SignatureAlgorithm.HS256).
                compact();
    }

    public String createToken(Map<String, Object> claims, String userName) {
        // Implementation to create a token with given claims and subject
        return null;
    }

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }   
}
