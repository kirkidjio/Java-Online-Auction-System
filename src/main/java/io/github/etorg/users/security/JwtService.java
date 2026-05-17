package io.github.etorg.users.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
	@Value("${security.jwt.secret-key}")
	String secretKey;
	
	@Value("${security.jwt.expiration-time}")
	long jwtExpiration;
	
	public UUID getUserId(String token) {
		return UUID.fromString(extractAllClaims(token).getSubject());
		
	}
	
	public boolean validate(String token) {
	    try {
	        extractAllClaims(token);
	        return true;
	    } catch (JwtException | IllegalArgumentException e) {
	        return false;
	    }
	}
	
	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSignInKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
		
	}
	
	public String buildToken(UUID userId) {
		return Jwts
				.builder()
				.setSubject(userId.toString())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256)
				.compact();
		
	}
	
	
	 
	private Key getSignInKey() {
		byte[] keybytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keybytes);
		
	}
}
	

