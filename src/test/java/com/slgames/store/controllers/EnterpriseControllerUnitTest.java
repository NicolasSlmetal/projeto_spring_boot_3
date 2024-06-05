package com.slgames.store.controllers;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.slgames.store.dtos.TypeDTO;
import com.slgames.store.dtos.enterprise.DefaultResponseEnterpriseDTO;
import com.slgames.store.dtos.enterprise.EnterpriseDTOFactory;
import com.slgames.store.dtos.enterprise.InsertEnterpriseDTO;
import com.slgames.store.dtos.enterprise.UpdateEnterpriseDTO;
import com.slgames.store.model.Enterprise;
import com.slgames.store.model.services.EnterpriseService;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@WebMvcTest(controllers = EnterpriseController.class)
@ActiveProfiles("test")
public class EnterpriseControllerUnitTest {
	
	@Autowired
	private EnterpriseController controller;
	
	@MockBean
	private EnterpriseService service;
	
	@Autowired
	private MockMvc mock;
	
	
	@BeforeEach
	public void setUp() {
		standaloneSetup(controller);
	}
	
	@Test
	@DisplayName("Should find all enterprises and Return Ok")
	public void testFindAllReturnOK() throws Exception {
		Enterprise enterprise = new Enterprise(1L, "Sample", LocalDate.of(1999, 1, 1));
		List<DefaultResponseEnterpriseDTO> dtoList = List.of((DefaultResponseEnterpriseDTO) 
				EnterpriseDTOFactory.createDTO(enterprise, TypeDTO.DEFAULT));
		
		when(service.findAll()).thenReturn(dtoList);
		
		mock.perform(get("/enterprise")).andExpect(status().isOk());
	}
	
	@Test
	@DisplayName("Should find a enterprise by ID and return OK")
	public void testEnterpriseByID() throws Exception {
		Long id = 1L;
		Enterprise enterprise = new Enterprise(1L, "Sample", LocalDate.of(1999, 1, 1));
		
		when(service.findById(id)).thenReturn(Optional.of(enterprise));
		
		mock.perform(get("/enterprise/{id}", id)).andExpect(status().isOk());
	
	}
	
	@Test
	@DisplayName("Should return status code Not Found when receive a non existing ID")
	public void testEnterpriseByIDReturnNotFound() throws Exception {
		Long id = -1L;
		
		when(service.findById(id)).thenReturn(Optional.empty());
		
		mock.perform(get("/enterprise/{id}", id)).andExpect(status().isNotFound());
	
	}
	
	@Test
	@DisplayName("Should return status code Created when insert a Enterprise")
	public void testInsertEnterpriseReturnCreated() throws Exception {
		InsertEnterpriseDTO dto = new InsertEnterpriseDTO("Sample", LocalDate.of(1999, 1, 1));
		Enterprise enterprise = new Enterprise(dto);
		enterprise.setId(1L);
		when(service.createEnterprise(dto)).thenReturn(enterprise);
		
		var response = mock.perform(post("/enterprise")
				.contentType(MediaType.APPLICATION_JSON)
				.content(dto.toString()))
		.andExpect(status().isCreated()).andReturn();
		String body = response.getResponse().getContentAsString();
		
		Assertions.assertTrue(body.contains("Sample"));
		
	}
	@Test
	@DisplayName("Should return status code Bad Request when a existing Enterprise name is sent")
	public void testInsertEnterpriseReturnBadRequestWhenAExistingEnterpriseNameIsSent() throws Exception {
		InsertEnterpriseDTO dto = new InsertEnterpriseDTO("Sample", LocalDate.of(1999, 1, 1));
		when(service.createEnterprise(dto)).thenThrow(new IllegalArgumentException(String.format("Enterprise name 'Sample' already exists")));
		
		mock.perform(post("/enterprise")
				.contentType(MediaType.APPLICATION_JSON)
				.content(dto.toString()))
		.andExpect(status().isBadRequest()).andReturn();
		
	}
	
	@Test
	@DisplayName("Should return status code Ok when a valid Body is provided")
	public void testUpdateEnterprise() throws Exception {
		UpdateEnterpriseDTO dto = new UpdateEnterpriseDTO(1L, "Updated Sample", LocalDate.of(1999, 1, 1));
		Enterprise enterprise = new Enterprise(1L, "Sample", LocalDate.of(1998, 1, 1));
		enterprise.update(dto);
		
		when(service.update(dto)).thenReturn(enterprise);
		
		var response = mock.perform(put("/enterprise")
				.contentType(MediaType.APPLICATION_JSON)
				.content(dto.toString()))
		.andExpect(status().isOk())
		.andReturn();
		String body = response.getResponse().getContentAsString();
		Assertions.assertTrue(body.contains("Updated Sample"));
	}
	
	@Test
	@DisplayName("Should return status code not found when a non existing ID is provided")
	public void testUpdateEnterpriseReturnNotFoundWhenNonExistingIDIsProvided() throws Exception {
		UpdateEnterpriseDTO dto = new UpdateEnterpriseDTO(-1L, "Updated Sample", LocalDate.of(1999, 1, 1));
		
		when(service.update(dto)).thenReturn(null);
		
		mock.perform(put("/enterprise")
					.contentType(MediaType.APPLICATION_JSON)
					.content(dto.toString()))
			.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Should return No Content when delete a Enterprise")
	public void testDeleteEnterprise() throws Exception {
		Long id = 1L;
		
		when(service.delete(id)).thenReturn(true);
		
		mock.perform(delete("/enterprise/{id}", id))
		.andExpect(status().isNoContent());
	}
	@Test
	@DisplayName("Should return Not Found when a non existing ID is provided")
	public void testDeleteEnterpriseReturnNotFound() throws Exception {
		Long id = -1L;
		
		when(service.delete(id)).thenReturn(false);
		
		mock.perform(delete("/enterprise/{id}", id))
		.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("Should return Internal Server Error when a DataViolationException is thrown, mainly in causes of trying to delete a referenced Enterprise")
	public void testDeleteEnterpriseInternalServerErrorWhenDataViolationExceptionIsThrown() throws Exception {
		Long id = 1L;
		
		when(service.delete(id)).thenThrow(new DataIntegrityViolationException("Cannot delete enterprise because there are games referenced to it"));
		
		mock.perform(delete("/enterprise/{id}", id))
		.andExpect(status().isInternalServerError());
	}
	
	
}
