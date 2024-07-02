package com.slgames.store.infra;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.slgames.store.model.services.LoginService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class BeforeRequestFilter extends OncePerRequestFilter{

	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private LoginService loginService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
			String endpoint = request.getRequestURI();
			String method = request.getMethod();
			if (!method.equalsIgnoreCase("GET") && !endpoint.equals("/login")) {
				String token = getTokenFromRequest(request);
				if (token != null) {
					String user = tokenService.retriveUserByToken(token);
					UserDetails userDetails = loginService.loadUserByUsername(user);
					if (userDetails != null) {
						Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
						SecurityContextHolder.getContext().setAuthentication(authentication);
					}
				}
			}
			filterChain.doFilter(request, response);
			
		
		
	}
	
	public String getTokenFromRequest(HttpServletRequest request){
		String token = request.getHeader("Authorization");
		if (token == null || token.length() < 7) return null;
		else {
			token = token.replace("Bearer", "").strip();
		}
		return token;
	}

}
