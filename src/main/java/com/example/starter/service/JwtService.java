package com.example.starter.service;

import com.example.starter.exception.TokenValidationException;
import com.example.starter.security.CredentialUserDetail;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

import java.security.Key;
import java.util.Date;

public class JwtService {
  private String jwtSecret;

  private Integer jwtExpirationMs;
  private static JwtService instance;
  private JwtService() {
    jwtSecret = "SuperSecret";
    jwtExpirationMs = 864000000;
  }

  public static JwtService getInstance() {
    if (instance == null)
      return new JwtService();
    return instance ;
  }
  public String generate(Authentication authentication) {
    return this.generate(extractPrincipal(authentication));
  }

  public String generate(String principal) {
    return Jwts.builder()
      .setSubject(principal)
      .setIssuedAt(new Date())
      .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
      .signWith(key(), SignatureAlgorithm.HS256)
      .compact();
  }

  public Key key() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(this.jwtSecret));
  }

  public String extract(String token) {
    return Jwts.parserBuilder()
      .setSigningKey(key())
      .build()
      .parseClaimsJws(token)
      .getBody()
      .getSubject();
  }

  public boolean validate(String token) throws TokenValidationException {
    try {
      Jwts.parserBuilder().setSigningKey(key()).build().parse(token);
      return true;
    } catch (MalformedJwtException exception) {
      throw new TokenValidationException("Invalid token");
    } catch (ExpiredJwtException exception) {
      throw new TokenValidationException("Expired token");
    } catch (UnsupportedJwtException exception) {
      throw new TokenValidationException("Unsupported token");
    } catch (IllegalArgumentException exception) {
      throw new TokenValidationException("Empty token");
    }
  }

  private String extractPrincipal(Authentication authentication) {
    return ((CredentialUserDetail) authentication.getPrincipal()).getUsername();
  }
}
