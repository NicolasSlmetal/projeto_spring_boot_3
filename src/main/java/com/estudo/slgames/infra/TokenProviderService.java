package com.estudo.slgames.infra;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.estudo.slgames.model.auth.User;

@Service
public class TokenProviderService {

	@Value("${api.security.token.secret}")
	private String secret;
	
	public String gerarToken(User user) {
		try {
		    var algorithm = Algorithm.HMAC256(secret);
		    return JWT.create()
		        .withIssuer("slgames-api")
		        .withSubject(user.getLogin())
		        .withClaim("id", user.getId())
		        .withExpiresAt(LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00")))
		        .sign(algorithm);
		} catch (JWTCreationException exception){
		    throw new RuntimeException("Erro ao gerar token", exception);
		}
	}
	public String verificarToken(String token) {
		try {
		    var algorithm = Algorithm.HMAC256(secret);
		    return JWT.require(algorithm)
		        .withIssuer("slgames-api")
		        .build()
		        .verify(token)
		        .getSubject();
		} catch (JWTVerificationException exception){
			throw new RuntimeException("Token inválido ou expirado");
		}
	}
}
