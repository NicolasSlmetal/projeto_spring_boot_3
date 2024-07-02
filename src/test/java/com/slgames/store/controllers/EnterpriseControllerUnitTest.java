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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slgames.store.dtos.enterprise.DefaultResponseEnterpriseDTO;
import com.slgames.store.dtos.enterprise.InsertEnterpriseDTO;
import com.slgames.store.dtos.enterprise.UpdateEnterpriseDTO;
import com.slgames.store.infra.SecurityConfigurationTest;
import com.slgames.store.model.Enterprise;
import com.slgames.store.model.services.EnterpriseService;
import com.slgames.store.utils.TestObjects;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

@WebMvcTest(controllers = EnterpriseController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Import(SecurityConfigurationTest.class)
public class EnterpriseControllerUnitTest {
	
	private static final String ENDPOINT = "/enterprises";

	@Autowired
	private EnterpriseController controller;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private MockMvc mock;
	
	@MockBean
	private EnterpriseService service;
	
	private TestObjects.EnterpriseTest enterpriseTest;
	
	
	@BeforeEach
	public void setUp() {
		standaloneSetup(controller);
		enterpriseTest = new TestObjects.EnterpriseTest();
	}
	
	@Test
	@DisplayName("Should find all enterprises and Return Ok")
	public void testFindAllReturnOK() throws Exception {
		Enterprise enterprise = enterpriseTest.createEnterprise();
		List<DefaultResponseEnterpriseDTO> dtoList = List.of(new 
				DefaultResponseEnterpriseDTO(enterprise));
		
		when(service.findAll()).thenReturn(dtoList);
		
		mock.perform(get(ENDPOINT)).andExpect(status().isOk());
	}
	
	@Test
	@DisplayName("Should find a enterprise by ID and return OK")
	public void testEnterpriseByID() throws Exception {
		Long id = 1L;
		Enterprise enterprise = enterpriseTest.createEnterprise();
		
		when(service.findById(id)).thenReturn(Optional.of(enterprise));
		
		mock.perform(get(ENDPOINT +"/{id}", id)).andExpect(status().isOk());
	
	}
	
	@Test
	@DisplayName("Should return status code Not Found when receive a non existing ID")
	public void testEnterpriseByIDReturnNotFound() throws Exception {
		Long id = -1L;
		
		when(service.findById(id)).thenReturn(Optional.empty());
		
		mock.perform(get(ENDPOINT +"/{id}", id)).andExpect(status().isNotFound());
	
	}
	
	@Test
	@DisplayName("Should return status code Created when insert a Enterprise")
	public void testInsertEnterpriseReturnCreated() throws Exception {
		InsertEnterpriseDTO dto = (InsertEnterpriseDTO) enterpriseTest.createdDTO();
		Enterprise enterprise = new Enterprise(dto);
		enterprise.setId(1L);
		when(service.createEnterprise(dto)).thenReturn(enterprise);
		String json = mapper.writeValueAsString(dto);
		var response = mock.perform(post(ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isCreated()).andReturn();
		String body = response.getResponse().getContentAsString();
		String expectedBody = mapper.writeValueAsString(enterpriseTest.expectedCreatedDTO());
		Assertions.assertEquals(expectedBody, body);
		
	}
	@Test
	@DisplayName("Should return status code Bad Request when a existing Enterprise name is sent")
	public void testInsertEnterpriseReturnBadRequestWhenAExistingEnterpriseNameIsSent() throws Exception {
		InsertEnterpriseDTO dto = (InsertEnterpriseDTO) enterpriseTest.createdDTO();
		when(service.createEnterprise(dto)).thenThrow(new IllegalArgumentException(String.format("Enterprise name 'Sample' already exists")));
		String json = mapper.writeValueAsString(dto);
		mock.perform(post(ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isBadRequest()).andReturn();
		
	}
	
	@Test
	@DisplayName("Should return status code Ok when a valid Body is provided")
	public void testUpdateEnterprise() throws Exception {
		UpdateEnterpriseDTO dto = (UpdateEnterpriseDTO) enterpriseTest.updatedDTO();
		Enterprise enterprise = enterpriseTest.createEnterprise();
		enterprise.update(dto);
		
		when(service.update(dto)).thenReturn(enterprise);
		String json = mapper.writeValueAsString(dto);
		var response = mock.perform(put(ENDPOINT)
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
		.andExpect(status().isOk())
		.andReturn();
		String body = response.getResponse().getContentAsString();
		String expectedBody = mapper.writeValueAsString(enterpriseTest.expectedDTO(dto.name()));
		Assertions.assertEquals(expectedBody, body);
	}
	
	@Test
	@DisplayName("Should return status code Not Found when a non existing ID is provided")
	public void testUpdateEnterpriseReturnNotFoundWhenNonExistingIDIsProvided() throws Exception {
		UpdateEnterpriseDTO dto = (UpdateEnterpriseDTO) enterpriseTest.updatedDTOWithInvalidID();
		
		when(service.update(dto)).thenReturn(null);
		String json = mapper.writeValueAsString(dto);
		mock.perform(put(ENDPOINT)
					.contentType(MediaType.APPLICATION_JSON)
					.content(json))
			.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Should return No Content when delete a Enterprise")
	public void testDeleteEnterprise() throws Exception {
		Long id = 1L;
		
		when(service.delete(id)).thenReturn(true);
		
		mock.perform(delete(ENDPOINT +"/{id}", id))
		.andExpect(status().isNoContent());
	}
	@Test
	@DisplayName("Should return Not Found when a non existing ID is provided")
	public void testDeleteEnterpriseReturnNotFound() throws Exception {
		Long id = -1L;
		
		when(service.delete(id)).thenReturn(false);
		
		mock.perform(delete(ENDPOINT +"/{id}", id))
		.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Should return Internal Server Error when a DataViolationException is thrown, mainly in causes of trying to delete a referenced Enterprise")
	public void testDeleteEnterpriseInternalServerErrorWhenDataViolationExceptionIsThrown() throws Exception {
		Long id = 1L;
		
		when(service.delete(id)).thenThrow(new DataIntegrityViolationException("Cannot delete enterprise because there are games referenced to it"));
		
		mock.perform(delete(ENDPOINT +"/{id}", id))
		.andExpect(status().isInternalServerError());
	}
	
	
}
