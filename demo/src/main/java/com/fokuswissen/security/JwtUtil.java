package com.fokuswissen.security;

import java.util.Date;
import java.util.Set;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import com.fokuswissen.user.UserRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil
{
    private final SecretKey secretKey;
    private final long EXPIRATION = 86400000;

    public JwtUtil()
    {
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public String generateToken(String username, Set<UserRole> roles , String id)
    {
        return Jwts.builder()
            .setSubject(username) //username in den Token
            .claim("id", id) //id in den Token
            .claim("roles", roles) //roles in den Token
            .setIssuedAt(new Date()) //Erstelldatum
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION)) //Ablaufdatum
            .signWith(secretKey, SignatureAlgorithm.HS512) //Sigantur
            .compact(); //Baut den Token
    }

    public String extractUsername(String token)
    {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token) //Build baut den Parser
            .getBody();
        return claims.getSubject();
    }

    public String extractId(String token)
    {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build() //Build baut den Parser
            .parseClaimsJws(token)
            .getBody();
        return claims.get("id", String.class);
    }

    public boolean validateToken(String token)
    {
        try
        {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build() //Build baut den Parser
                .parseClaimsJws(token); //wenn Token durch den Parser kommt ist er validiert
            return true;
        }
        catch (JwtException | IllegalArgumentException e)
        {
            return false;
        }
    }
}
