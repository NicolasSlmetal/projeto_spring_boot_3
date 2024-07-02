package com.slgames.store.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slgames.store.dtos.users.InsertUserDTO;
import com.slgames.store.dtos.users.UpdateUserDTO;
import com.slgames.store.dtos.users.UpdatedUserResponseDTO;
import com.slgames.store.infra.SecurityConfigurationTest;
import com.slgames.store.infra.TokenService;
import com.slgames.store.model.User;
import com.slgames.store.model.services.UserService;
import com.slgames.store.utils.TestObjects;


import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;

@WebMvcTest(controllers = UserController.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Import(SecurityConfigurationTest.class)
public class UserControllerUnitTest {

	private static final String ENDPOINT = "/users";

	@Autowired
	private UserController controller;
	
	@Autowired
	private MockMvc mock;
	
	@Autowired
	private ObjectMapper mapper;
	@MockBean
	private UserService service; 
	@MockBean
	private TokenService tokenService;
	
	private TestObjects.UserTest userTest;
	
	@BeforeEach
	public void setUp() {
		standaloneSetup(controller);
		userTest = new TestObjects.UserTest();
	}
	
	@Test
	@DisplayName("Should return status code Created")
	public void testInsertUserReturnCreated() throws Exception {
		InsertUserDTO dto = (InsertUserDTO) userTest.createdDTO();
		User user = new User(dto);
		
		user.setId(1L);
		when(service.createUser(dto)).thenReturn(user);
		String json = mapper.writeValueAsString(dto);
		var response = mock.perform(post(ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isCreated()).andReturn();
		String body = response.getResponse().getContentAsString();
		String expectedBody = mapper.writeValueAsString(userTest.expectedCreatedDTO());
		Assertions.assertEquals(expectedBody, body);
	}
	
	@Test
	@DisplayName("Should return status code Not found when a user with ADM role is sent to insert")
	public void testInsertUserReturnBadRequestWhenRoleADMIsSet() throws Exception{
		InsertUserDTO dto = (InsertUserDTO) userTest.createdDTOWithADMRole();
		when(service.createUser(dto)).thenThrow(new IllegalArgumentException("Cannot create user with ADM role"));
		String json = mapper.writeValueAsString(dto);
		mock.perform(post(ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isBadRequest()).andReturn();
	}
	@Test
	@DisplayName("Should return status code Ok")
	public void testUpdateUserReturnOk() throws Exception {
		UpdateUserDTO dto = (UpdateUserDTO) userTest.updatedDTO();
		User user = userTest.createUser();
		user.updateUser(dto);
		UpdatedUserResponseDTO expectedResponse = (UpdatedUserResponseDTO) userTest.expectedUpdatedUserResponse();
		when(service.updateUser(dto)).thenReturn(user);
		when(service.refreshTokenToUpdatedUser(user)).thenReturn(expectedResponse);
		String json = mapper.writeValueAsString(dto);
		var response = mock.perform(put(ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isOk()).andReturn();
		String body = response.getResponse().getContentAsString();
		String expectedBody = mapper.writeValueAsString(expectedResponse);
		Assertions.assertEquals(expectedBody, body);
		
	}
	
	@Test
	@DisplayName("Should return status code No content")
	public void testDeleteUserReturnStatusCodeNoContent() throws Exception {
		Long id = 1L;
		when(service.deleteUser(id)).thenReturn(true);
		mock.perform(delete(ENDPOINT + "/{id}", id))
		.andExpect(status().isNoContent());
	}
}
