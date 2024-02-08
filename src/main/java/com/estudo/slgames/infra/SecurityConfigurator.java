package com.estudo.slgames.infra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfigurator {
	
	@Autowired
	private FiltroSeguranca filtroSeguranca;
	
	@Bean
	public SecurityFilterChain filtrarSeguranca(HttpSecurity http) throws Exception {
		return http.csrf(csrf -> csrf.disable())
				.sessionManagement(sessionManagement -> 
				sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> 
				auth.requestMatchers(HttpMethod.POST, "/login").permitAll()
				.requestMatchers(HttpMethod.GET, "/swagger-ui.html").permitAll()
				.requestMatchers(HttpMethod.GET, "/swagger-ui/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/v3/api-docs/**").permitAll()
				.anyRequest().authenticated())
				.addFilterBefore(filtroSeguranca, UsernamePasswordAuthenticationFilter.class)
				.build();
	}
	
	@Bean
	public AuthenticationManager providenciarManager(AuthenticationConfiguration configuration) throws Exception {
		
		return configuration.getAuthenticationManager();
	}
	
	@Bean
	public PasswordEncoder encriptografar() {
		return new BCryptPasswordEncoder();
	}
	
}
