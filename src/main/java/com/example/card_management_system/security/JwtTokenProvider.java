package com.example.card_management_system.security;

import com.example.card_management_system.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Service responsible for generating, validating, and extracting information from JSON Web Tokens (JWT).
 * This class provides methods to create a JWT token for authentication, retrieve the email of the user from a token,
 * and validate the integrity of a token.
 * <p>
 * The JWT token is signed with a secret key and contains user information such as email and role.
 */
@Component
public class JwtTokenProvider{

    /**
     * The secret key used to sign the JWT token.
     */
    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * The expiration time of the JWT token in milliseconds.
     */
    @Value("${jwt.expirationMs}")
    private int jwtExpirationMs;

    /**
     * Generates a JWT token for the authenticated user.
     * The token contains the user's email and role, and is signed using the configured secret key.
     * The token expires after the specified expiration time.
     *
     * @param authentication the Authentication object containing the user details
     * @return the generated JWT token as a String
     */
    public String generateToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getAuthorities().toString())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    /**
     * Extracts the email address from the provided JWT token.
     *
     * @param token the JWT token
     * @return the email of the user contained in the token
     */
    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Extracts all claims (user data) from the provided JWT token.
     *
     * @param token the JWT token
     * @return a Claims object containing all the information from the token
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Validates the provided JWT token. It checks whether the token is correctly signed and whether it has expired.
     *
     * @param authToken the JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            e.fillInStackTrace();
        }
        return false;
    }
}
