package com.uber.services;

import com.uber.Roles;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JWTUtil {


    @Value("${jwt.secretkey}")
    private String SecretKey;

    private Key getKey(){
      return Keys.hmacShaKeyFor(SecretKey.getBytes());
    }
    public String generateToken(String email, Roles role){
        return Jwts.builder()
                .setSubject(email)
                .claim("role",role.name())
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 *60 * 60 *10))
                .compact();
    }
    public String getEmailFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    public Date getExpirationDateFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
    public String getRoleFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role").toString();
    }
    public boolean isTokenExpired(String token){
        return getExpirationDateFromToken(token).before(new Date());
    }


}
