package com.werecover.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    private final String SECRET_KEY;

    // Inject secret key from application.properties
    public JwtService(@Value("${JWT_SECRET:}") String secretKey) {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException("JWT_SECRET is missing! Set it as an environment variable.");
        }
        this.SECRET_KEY = secretKey;
    }

    // ✅ Generate JWT with email and role
    public String generateToken(String email, String role) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role) // ✅ Store role inside JWT
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24-hour expiry
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ Extract email from JWT
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ✅ Extract role from JWT
    public String extractRole(String token) {
        try {
            return extractClaim(token, claims -> claims.get("role", String.class));
        } catch (Exception e) {
            System.out.println("❌ Error extracting role from JWT: " + e.getMessage());
            return null;
        }
    }

    // ✅ Validate JWT token
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractEmail(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // ✅ Check if JWT is expired
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    // ✅ Generic method to extract claims
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claimsResolver.apply(claims);
    }

    // ✅ Convert SECRET_KEY into a valid signing key
    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

