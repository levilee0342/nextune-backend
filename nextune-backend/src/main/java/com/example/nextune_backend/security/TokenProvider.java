package com.example.nextune_backend.security;

import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.access-expires-in-min:10}")
    private long accessExpMin;

    @Value("${app.jwt.refresh-expires-in-days:7}")
    private long refreshExpDays;

    @Value("${app.jwt.session-expires-in-days:7}")
    private long sessionExpDays;

    public String generateAccessToken(Authentication authentication) {
        Date expiryDate = new Date(new Date().getTime() + accessExpMin* 60 * 1000);
        System.out.println("Issued at: " + new Date());
        System.out.println("Expires at: " + expiryDate);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String generateRefreshToken(String email) {
        Instant now = Instant.now();
        Instant exp = now.plus(refreshExpDays, ChronoUnit.DAYS);
        System.out.println("Refresh token issued at: " + now);
        System.out.println("Refresh token expires at: " + exp);

        return Jwts.builder()
                .setSubject(email)
                .setId(UUID.randomUUID().toString()) // jti to prevent reuse
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(refreshExpDays, ChronoUnit.DAYS)))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String generateSessionToken(String email) {
        Instant now = Instant.now();
        Instant exp = now.plus(sessionExpDays, ChronoUnit.MINUTES); 
        System.out.println("Session token issued at: " + now);
        System.out.println("Session token expires at: " + exp);

        return Jwts.builder()
                .setSubject(email)
                .setId(UUID.randomUUID().toString()) // jti để chống reuse
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(sessionExpDays, ChronoUnit.DAYS)))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        return false;
    }

    public String getSubject(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Instant getExpiration(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration().toInstant();
    }
}
