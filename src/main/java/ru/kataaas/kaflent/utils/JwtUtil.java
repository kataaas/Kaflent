package ru.kataaas.kaflent.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

@Component
public class JwtUtil implements Serializable {

    @Value("${app.jwt-token-validity}")
    public long JWT_TOKEN_VALIDITY;

    @Value("${app.jwt-secret-token}")
    private String JWT_SECRET_TOKEN;

    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(JWT_SECRET_TOKEN)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String generateToken(UserDetails userDetails) {
        String username = userDetails.getUsername();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET_TOKEN).compact();
    }

    public boolean validityToken(String token, UserDetails userDetails) {
        String username = getUsernameFromJwtToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Date getExpirationDateFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(JWT_SECRET_TOKEN).parseClaimsJws(token).getBody();
    }

}
