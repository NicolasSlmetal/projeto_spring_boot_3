package com.slgames.store.infra;


import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;


@TestConfiguration
@ActiveProfiles("test")
public class SecurityConfigurationTest {
	
	@MockBean
	private BeforeRequestFilter beforeFilter;
	
	@MockBean
	private UsersEndpointFilter userEndpointFilter;
	@Bean
	SecurityFilterChain configure(HttpSecurity http) throws Exception {
		return http.csrf(csrf -> csrf.disable())
				.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
				.build();
	}
	
}
