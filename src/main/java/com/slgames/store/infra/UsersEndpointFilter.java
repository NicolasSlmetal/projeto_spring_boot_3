package com.slgames.store.infra;


import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slgames.store.model.User;
import com.slgames.store.model.services.LoginService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;

@Component
@Getter
public class UsersEndpointFilter extends OncePerRequestFilter{

	@Autowired
	private LoginService service;
	
	@Autowired
	private ObjectMapper mapper;
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		CachedBodyHttpRequest wrapper = new CachedBodyHttpRequest((HttpServletRequest) request);
		String endpoint = wrapper.getRequestURI();
		if (endpoint.startsWith("/users")) {
			String method = wrapper.getMethod();
			if (!method.equalsIgnoreCase("GET") && !method.equalsIgnoreCase("POST")) {
				Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
				
				if (authentication == null) {
					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					return;
				
				}
				for (GrantedAuthority authority : authentication.getAuthorities()) {
					if (authority.getAuthority().equals("ADM")) {
						filterChain.doFilter(wrapper, response);
						return;
					}
				}
				User user = (User) getService().loadUserByUsername(authentication.getName());
				Long providedId = null;
				if (method.equalsIgnoreCase("PUT")) {
					 
					 providedId = parseIdFromBody(wrapper);
				}else if (method.equalsIgnoreCase("DELETE")) {
					providedId = parseIdFromPath(endpoint);
				}
				if (user.getId() != providedId) {
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					return;
				}
			}
		}
		filterChain.doFilter(wrapper, response);
		
	}
	

	public Long parseIdFromBody(HttpServletRequest request) {
		try {
			String body = getBody(request);
			return mapper.readTree(body).get("id").asLong();
		} catch (IOException ioException) {
			ioException.printStackTrace();
			return -1L;
		}
	}


	public String getBody(HttpServletRequest request) throws IOException {
		StringBuffer sb = new StringBuffer();
		try(InputStream inputStream = request.getInputStream();){
			int b;
			while ((b = inputStream.read()) != -1) {
				sb.append((char) b);
			}
		}
		return sb.toString(); 
		
	}
	
	public Long parseIdFromPath(String request) {
		String[] split = request.split("/");
		return Long.parseLong(split[2]);
	}

}
