package com.slgames.store.controllers;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.slgames.store.infra.TokenService.TokenDTO;

import java.net.URI;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slgames.store.dtos.users.LoginUserDTO;
import com.slgames.store.infra.SecurityConfigurationTest;
import com.slgames.store.infra.TokenService;
import com.slgames.store.model.User;
import com.slgames.store.model.services.LoginService;
import com.slgames.store.utils.TestObjects;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;

@ActiveProfiles("test")
@WebMvcTest(controllers = LoginController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(SecurityConfigurationTest.class)
public class LoginControllerUnitTest {

	@Autowired
	private LoginController controller;
	
	@Autowired
	private MockMvc mock;
	
	@Autowired
	private ObjectMapper mapper;
	
	@MockBean
	private LoginService service;
	
	@MockBean
	private AuthenticationManager manager;
	
	@MockBean
	private TokenService tokenService;
	
	private TestObjects.LoginTest loginTest;
	
	
	@BeforeEach
	public void setUp() {
		standaloneSetup(controller);
		loginTest = new TestObjects.LoginTest();
	}
	
	@Test
	@DisplayName("Should return Ok when a valid credentials is sent")
	public void testLoginReturnOkWhenExistingNicknameAndEmailIsProvided() throws Exception {
		LoginUserDTO loginDTO = loginTest.createLoginTest();
		String json = mapper.writeValueAsString(loginDTO);
		
		when(service.getDefaultNickname(loginDTO)).thenReturn("Sample");
		Authentication authentication = mock(Authentication.class);
		when(manager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
		User user = mock(User.class);
		when(authentication.getPrincipal()).thenReturn(user);
		TokenDTO tokenDto = new TokenDTO("token");
		when(tokenService.generateTokenForAuthenticatedUser(user)).thenReturn(tokenDto);
		
		var response = mock.perform(post(URI.create("/login"))
				
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isOk()).andReturn().getResponse();
		String body = response.getContentAsString();
		String expectedBody = mapper.writeValueAsString(tokenDto);
		Assertions.assertEquals(expectedBody, body);
	}
	
	@Test
	@DisplayName("Should return Ok when only valid nickname and password is sent")
	public void testLoginReturnOkWhenOnlyExistingNicknameAndPasswordIsProvided() throws Exception {
		LoginUserDTO loginDTO = loginTest.createLoginTestWithoutEmail();
		String json = mapper.writeValueAsString(loginDTO);
		
		when(service.getDefaultNickname(loginDTO)).thenReturn("Sample");
		Authentication authentication = mock(Authentication.class);
		when(manager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
		User user = mock(User.class);
		when(authentication.getPrincipal()).thenReturn(user);
		TokenDTO tokenDto = new TokenDTO("token");
		when(tokenService.generateTokenForAuthenticatedUser(user)).thenReturn(tokenDto);
		
		var response = mock.perform(post(URI.create("/login"))
				
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isOk()).andReturn().getResponse();
		String body = response.getContentAsString();
		String expectedBody = mapper.writeValueAsString(tokenDto);
		Assertions.assertEquals(expectedBody, body);
	}
	
	@Test
	@DisplayName("Should return Ok when only valid email and password is sent")
	public void testLoginReturnOkWhenOnlyExistingEmailAndPasswordIsProvided() throws Exception {
		LoginUserDTO loginDTO = loginTest.createLoginTestWithoutNickname();
		String json = mapper.writeValueAsString(loginDTO);
		
		when(service.getDefaultNickname(loginDTO)).thenReturn("Sample@mail.com");
		Authentication authentication = mock(Authentication.class);
		when(manager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
		User user = mock(User.class);
		when(authentication.getPrincipal()).thenReturn(user);
		TokenDTO tokenDto = new TokenDTO("token");
		when(tokenService.generateTokenForAuthenticatedUser(user)).thenReturn(tokenDto);
		
		var response = mock.perform(post(URI.create("/login"))
				
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isOk()).andReturn().getResponse();
		String body = response.getContentAsString();
		String expectedBody = mapper.writeValueAsString(tokenDto);
		Assertions.assertEquals(expectedBody, body);
	}
	
	@Test
	@DisplayName("Should return Bad request when email and nickname is not provided")
	public void testLoginReturnBadRequestWhenEmailAndNicknameIsNotProvided() throws Exception {
		LoginUserDTO loginDTO = new LoginUserDTO(null, null, "12345");
		String json = mapper.writeValueAsString(loginDTO);
		doThrow(new IllegalArgumentException("Nickname or email not provided"))
		.when(service).validateNicknameOrEmailPresent(loginDTO);
		mock.perform(post(URI.create("/login"))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isBadRequest());
	}
}
