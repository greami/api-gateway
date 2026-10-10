package org.api.gateway.adapter.security;

import org.api.gateway.application.port.out.JwtTokenPort;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService implements JwtTokenPort {
    private final String secret;
    private final long expirationMs;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration-ms}") long expirationMs)
    {
        this.secret = secret;
        this.expirationMs = expirationMs;
    }

    @Override
    public String generateToken(String username) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(signingKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token)
    {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails)
    {
        return userDetails.getUsername().equals(extractUsername(token)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token)
    {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver)
    {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token)
    {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private SecretKey signingKey()
    {
        byte[] keyBytes;
        try
        {
            keyBytes = Decoders.BASE64.decode(secret);
        }
        catch (IllegalArgumentException ex)
        {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }
}
