package com.estudo.slgames.infra;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.estudo.slgames.model.auth.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import springfox.documentation.annotations.ApiIgnore;

@Component
@ApiIgnore
public class FiltroSeguranca extends OncePerRequestFilter{

	
	@Autowired
	private TokenProviderService tokenService;
	@Autowired
	private UserRepository repository;
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		
		var tokenJWT = recuperarToken(request);
		if (tokenJWT != null) {
			var subject = getSubject(tokenJWT);
			var user = repository.findByLogin(subject);
			var auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(auth);
		}
		filterChain.doFilter(request, response);
		
	}
	
	private String getSubject(String tokenJWT) {
		return tokenService.verificarToken(tokenJWT);
	}
	
	private String recuperarToken(HttpServletRequest request) {
		var authorizationHeader = request.getHeader("Authorization");
		if (authorizationHeader != null) {
			return authorizationHeader.replace("Bearer", "").strip();
		}
		
		return null;
	}
}
