package com.slgames.store.controllers;

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
import com.slgames.store.dtos.game.DefaultResponseGameDTO;
import com.slgames.store.dtos.game.InsertGameDTO;
import com.slgames.store.dtos.game.UpdateGameDTO;
import com.slgames.store.infra.SecurityConfigurationTest;
import com.slgames.store.model.Game;
import com.slgames.store.model.services.GameService;
import com.slgames.store.utils.TestObjects;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;

@ActiveProfiles("test")
@WebMvcTest(controllers = GameController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(SecurityConfigurationTest.class)
public class GameControllerUnitTest {
	
	private static final String ENDPOINT = "/games";
	
	@Autowired
	private MockMvc mock;
	@Autowired 
	private GameController controller;
	
	@Autowired
	private ObjectMapper mapper;
	
	@MockBean
	private GameService service;
	
	private TestObjects.GameTest gameTest;
	
	@BeforeEach
	public void setUp() {
		standaloneSetup(controller);
		gameTest = new TestObjects.GameTest();
	}
	
	@Test
	@DisplayName("Should return status code Created when a valid body is sent")
	void testInsertGameReturnCreated() throws Exception {
		InsertGameDTO insertDto = (InsertGameDTO) gameTest.createdDTO();
		Game game = new Game(insertDto);
		game.setId(1L);
		String json = mapper.writeValueAsString(insertDto);
		when(service.createGame(insertDto)).thenReturn(game);
		var response = mock.perform(post(URI.create(ENDPOINT))
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
				
		.andExpect(status().isCreated()).andReturn();
		String body = response.getResponse().getContentAsString();
		String expectedBody = mapper.writeValueAsString(gameTest.expectedCreatedDTO());
		Assertions.assertEquals(expectedBody, body);
	}
	
	@Test
	@DisplayName("Should return status code Bad Request when a invalid title is sent")
	void testInsertGameReturnBadRequestWhenInvalidTitleIsSent() throws Exception {
		InsertGameDTO insertDto = (InsertGameDTO) gameTest.createdDTOWithBlankName();
		when(service.createGame(insertDto)).thenReturn(new Game(insertDto));
		mock.perform(post(URI.create(ENDPOINT))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(insertDto.toString()))
		.andExpect(status().isBadRequest());
	}
	
	@Test
	@DisplayName("Should return status code Bad Request when a invalid developer ID is sent")
	void testInsertGameReturnBadRequestWhenInvalidDeveloperIdIsSent() throws Exception {
		InsertGameDTO insertDto = (InsertGameDTO) gameTest.createdDTOWithInvalidDeveloperId();
		when(service.createGame(insertDto)).thenThrow(new IllegalArgumentException("No existing developer id"));
		String json = mapper.writeValueAsString(insertDto);
		mock.perform(post(URI.create(ENDPOINT))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isBadRequest());
	}
	
	@Test
	@DisplayName("Should return status code Created when a invalid publisher ID is sent")
	void testInsertGameReturnBadRequestWhenInvalidPublisherIdIsSent() throws Exception {
		InsertGameDTO insertDto = (InsertGameDTO) gameTest.createdDTOWithInvalidPublisherId();
		when(service.createGame(insertDto)).thenThrow(new IllegalArgumentException("No existing publisher id"));
		String json = mapper.writeValueAsString(insertDto);
		mock.perform(post(URI.create(ENDPOINT))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isBadRequest());
	}
	
	@Test
	@DisplayName("Should return status code Bad Request when a negative price is sent")
	void testInsertGameReturnBadRequestWhenNegativePriceIsSent() throws Exception {
		InsertGameDTO insertDto = (InsertGameDTO) gameTest.createdDTOWithNegativePrice();
		when(service.createGame(insertDto)).thenThrow(new IllegalArgumentException("Price must be not negative"));
		String json = mapper.writeValueAsString(insertDto);
		mock.perform(post(URI.create(ENDPOINT))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isBadRequest());
	}
	
	@Test
	@DisplayName("Should return status code Ok when call with a existing ID")
	public void testFindByIdReturnOkWhenExistingIdIsSent() throws Exception {
		when(service.findById(1L))
		.thenReturn(Optional.of(gameTest.createGame()));
		mock.perform(get(ENDPOINT + "/{id}", 1L)).andExpect(status().isOk());
		
	}
	@Test
	@DisplayName("Should return status code Not Found when call with a non existing ID")
	public void testFindByIdReturnNotFoundWhenNonExistingIDIsSent() throws Exception {
		when(service.findById(-1L))
		.thenReturn(Optional.empty());
		mock.perform(get(ENDPOINT + "/{id}", -1L)).andExpect(status().isNotFound());
		
	}
	
	@Test
	@DisplayName("Should return status code Ok")
	public void testFindAllReturnOk() throws Exception {
		List<DefaultResponseGameDTO> dtos = List.of((DefaultResponseGameDTO)gameTest.expectedDTO());
		when(service.findAll()).thenReturn(dtos);
		mock.perform(get(ENDPOINT)).andExpect(status().isOk());
	}
	
	@Test
	@DisplayName("Should return status code Ok when a valid body to update is sent")
	public void testUpdateGameReturnOkWhenAValidBodyIsProvided() throws Exception {
		UpdateGameDTO dto = (UpdateGameDTO) gameTest.updatedDTO();
		Game game = gameTest.createGame();
		game.update(dto);
		String json = mapper.writeValueAsString(dto);
		when(service.update(dto)).thenReturn(game);
		var response = mock.perform(put(ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isOk()).andReturn();
		
		String body = response.getResponse().getContentAsString();
		String expectedBody = mapper.writeValueAsString(gameTest.expectedDTO(dto.title(), dto.price()));
		Assertions.assertEquals(expectedBody, body);
	}
	
	@Test
	@DisplayName("Should return status code Not Found when a invalid ID is sent")
	public void testUpdateGameReturnNotFoundWhenAInvalidIDIsSent() throws Exception {
		UpdateGameDTO game = (UpdateGameDTO) gameTest.updatedDTOWithInvalidID();
		String json = mapper.writeValueAsString(game);
		when(service.update(game)).thenReturn(null);
		mock.perform(put(ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isNotFound());
		
	}
	@Test
	@DisplayName("Should return status code Bad Request when a null ID is sent")
	public void testUpdateGameReturnBadRequestWhenANullIDIsSent() throws Exception {
		UpdateGameDTO game = (UpdateGameDTO) gameTest.updatedDTOWithNullID();
		String json = mapper.writeValueAsString(game);
		mock.perform(put(ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isBadRequest());
	}
	
	@Test
	@DisplayName("Should return status code No Content when a existing ID is sent")
	public void testDeleteGameReturnNoContentWhenAExistingIDIsSent() throws Exception {
		long id = 1L;
		when(service.delete(id)).thenReturn(true);
		mock.perform(delete(ENDPOINT + "/{id}", id)).andExpect(status().isNoContent());
	}
	
	@Test
	@DisplayName("Should return status code Not Found when a Non existing ID is sent")
	public void testDeleteGameReturnNotFoundWhenANonExistingIDIsSent() throws Exception {
		long id = -1L;
		when(service.delete(id)).thenReturn(false);
		mock.perform(delete(ENDPOINT + "/{id}", id)).andExpect(status().isNotFound());
	}
	
}
