package com.slgames.store.infra;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.slgames.store.model.User;

@Service
public class TokenService {

	public static long VALID_TIME_FOR_AUTHENTICATION = 1L;
	
	@Value("${api.key}")
	private String key;
	
	public TokenDTO generateTokenForAuthenticatedUser(User user) {
		try {
			Instant expiration = Instant.now().plus(VALID_TIME_FOR_AUTHENTICATION, ChronoUnit.HOURS);
		    Algorithm algorithm = Algorithm.HMAC256(key);
		    String token = JWT.create()
		        .withIssuer("auth0")
		        .withSubject(user.getUsername())
		        .withExpiresAt(expiration)
		        .sign(algorithm);
		
		    return new TokenDTO(token);
		} catch (JWTCreationException exception){
		    throw new RuntimeException("Error while generating token:" + exception.getMessage());
		}
	}
	
	public String retriveUserByToken(String token) {
		DecodedJWT decodedJWT;
		try {
		    Algorithm algorithm = Algorithm.HMAC256(key);
		    JWTVerifier verifier = JWT.require(algorithm)
		        .withIssuer("auth0")
		        .build();
		        
		    decodedJWT = verifier.verify(token);
		    return decodedJWT.getSubject();
		} catch (JWTVerificationException exception){
		    throw new IllegalArgumentException("Error while verifying a token: ".concat(exception.getMessage()));
		}
	}
	
	
	
	public record TokenDTO(String token, LocalDateTime expiration) {
		
		public TokenDTO(String token){
			this(token, LocalDateTime.now().plusHours(VALID_TIME_FOR_AUTHENTICATION));
		}
	}
	
}
