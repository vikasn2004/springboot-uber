package com.uber.services;

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

    private Key getkey(){
      return Keys.hmacShaKeyFor(SecretKey.getBytes());
    }
    public String generateToken(String email){
        return Jwts.builder()
                .setSubject(email)
                .signWith(getkey(), SignatureAlgorithm.HS256)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 *60 * 60 *10))
                .compact();
    }
    public String getEmailFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getkey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    public Date getExpirationDateFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getkey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }
    public boolean TokenExpired(String token){
        return getExpirationDateFromToken(token).before(new Date());
    }
    public boolean validateToken(String token,UserDetails userDetails){
        String email = getEmailFromToken(token);
        return email.equals(userDetails.getUsername())  && !TokenExpired(token);
    }

}
