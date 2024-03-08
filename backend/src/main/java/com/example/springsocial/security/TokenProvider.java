package com.example.springsocial.security;

import com.example.springsocial.config.AppProperties;
import com.example.springsocial.model.AppUserRole;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
 
import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class TokenProvider {
    private static final String SECRET_KEY = "your_secret_key_here"; // Replace with your own secret key
    private   Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

    private AppProperties appProperties;

    public TokenProvider(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public String createToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() +3600000 + appProperties.getAuth().getTokenExpirationMsec());
        // Date now = new Date();
        // // Set expiration date to one day (24 hours) from the current time
        // Date expiryDate = new Date(now.getTime() + 24 * 60 * 60 * 1000);
    
        // Generate a secure key for HS512 algorithm
        // Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
      System.out.println(expiryDate.getTime()+"expire date ");
        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public int getUserIdFromToken(String token) {
        
        Claims claims = Jwts.parser()
        
                .setSigningKey(key )
                .parseClaimsJws(token)
                .getBody();

        return Integer.parseInt(claims.getSubject());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(authToken);
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

}
