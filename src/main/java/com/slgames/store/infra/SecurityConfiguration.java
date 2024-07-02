package com.slgames.store.infra;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;



@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	private static final String USER_ENDPOINT = "/users";
	private static final String ENTERPRISE_ENDPOINT = "/enterprises";
	private static final String GAME_ENDPOINT = "/games";
	
	@Autowired
	public CharacterEncodingFilter encodingFilter;
	@Autowired
	public BeforeRequestFilter beforeFilter;
	
	@Autowired
	public UsersEndpointFilter userEndpointFilter;
	
	@Bean
	SecurityFilterChain configure(HttpSecurity http) throws Exception {
		return http
				.sessionManagement(session ->
				session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(
				auth -> auth
				.requestMatchers(HttpMethod.GET)
				.permitAll()
				.requestMatchers(HttpMethod.POST, GAME_ENDPOINT)
				.hasAuthority("STAFF")
				//the enterprise endpoint with POST, PUT or DELETE methods can be used only for ADM
				.requestMatchers(HttpMethod.POST, ENTERPRISE_ENDPOINT)
				.hasAuthority("ADM")
				.requestMatchers(HttpMethod.PUT, GAME_ENDPOINT)
				.hasAuthority("STAFF")
				.requestMatchers(HttpMethod.PUT, ENTERPRISE_ENDPOINT)
				.hasAuthority("ADM")
				//Only ADM can use DELETE methods, unless in the case a user will delete your own account
				.requestMatchers(HttpMethod.DELETE, USER_ENDPOINT + "/{id}")
				.permitAll()
				.requestMatchers(HttpMethod.DELETE)
				.hasAuthority("ADM")
				
				.anyRequest().permitAll())
				.addFilterBefore(encodingFilter, CsrfFilter.class)
				.addFilterBefore(beforeFilter, UsernamePasswordAuthenticationFilter.class)
				.addFilterAfter(userEndpointFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}

	@Bean
	AuthenticationManager getAuthenticationManager(UserDetailsService userDetailsService,
			PasswordEncoder encoder) {
		
		DaoAuthenticationProvider dao = new DaoAuthenticationProvider();
		dao.setUserDetailsService(userDetailsService);
		dao.setPasswordEncoder(encoder);
		ProviderManager manager = new ProviderManager(dao);
		manager.setEraseCredentialsAfterAuthentication(false);
		return manager;
	}
	
	
	@Bean
	PasswordEncoder encode() {
		return new BCryptPasswordEncoder();
	}
	

}
