package com.yellow.foxbuy.utils;

import com.yellow.foxbuy.models.User;
import io.jsonwebtoken.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;

import java.time.temporal.ChronoUnit;

import java.util.Base64;
import java.util.Collection;
import java.util.Date;


@Component
public class JwtUtil {

    String secret = System.getenv("JWT_SECRET_KEY");

    Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(secret), SignatureAlgorithm.HS256.getJcaName());

    public String createToken(User user) {
        Instant now = Instant.now();
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();

        StringBuilder authorityString = new StringBuilder();
        for (GrantedAuthority authority : authorities) {
            authorityString.append(authority.getAuthority()).append(",");
        }

        if (authorityString.length() > 0) {
            authorityString.deleteCharAt(authorityString.length() - 1);
        }

        String jwtToken = Jwts.builder()
                .claim("username", user.getUsername())
                .claim("email", user.getEmail())
                .claim("authorities", authorityString.toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(3000, ChronoUnit.MINUTES)))
                .signWith(hmacKey)
                .setHeaderParam("typ", "JWT")
                .compact();
        return jwtToken;
    }


    public Jws<Claims> validateJwt(String jwtString) throws SecurityException{
        Jws<Claims> jwt = Jwts.parserBuilder()
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(jwtString);
        return jwt;
    }
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(hmacKey)
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            // Invalid signature/claims
        } catch (ExpiredJwtException ex) {
            // Expired token
        } catch (UnsupportedJwtException ex) {
            // Unsupported JWT token
        } catch (MalformedJwtException ex) {
            // Malformed JWT token
        } catch (IllegalArgumentException ex) {
            // JWT token is empty
        }
        return false;
    }
    public String getUsernameFromJWT(String token) throws SecurityException {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return (String) claims.get("username");
    }
    public Date extractExpiration(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration();
    }
    public String getAuthoritiesFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return (String) claims.get("authorities").toString();
    }
}