package com.slgames.store.model.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import com.slgames.store.dtos.TypeDTO;
import com.slgames.store.dtos.enterprise.DefaultResponseEnterpriseDTO;
import com.slgames.store.dtos.enterprise.EnterpriseDTOFactory;
import com.slgames.store.dtos.enterprise.InsertEnterpriseDTO;
import com.slgames.store.dtos.enterprise.UpdateEnterpriseDTO;
import com.slgames.store.infra.WebMvcConfiguration;
import com.slgames.store.model.Enterprise;
import com.slgames.store.model.repository.EnterpriseRepository;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.standaloneSetup;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class EnterpriseServiceTest {

	@Autowired
	private EnterpriseService service;
	
	@MockBean
	private EnterpriseRepository repository;
	
	@BeforeEach
	public void setUp() {
		standaloneSetup(service);
	}
	@Test
	@DisplayName("Should insert a Enterprise")
	public void testCreateEnterprise() throws CloneNotSupportedException {
		InsertEnterpriseDTO dto = new InsertEnterpriseDTO("Sample", LocalDate.of(1999, 1, 1));
		Enterprise enterprise = new Enterprise(dto);
		Enterprise persistedEnterprise = enterprise.clone();
		persistedEnterprise.setId(1L);
		when(repository.existsByName(dto.name())).thenReturn(false);
		when(repository.save(enterprise)).thenReturn(persistedEnterprise);
		
		Enterprise savedEnterprise = service.createEnterprise(dto);
		Assertions.assertEquals(persistedEnterprise, savedEnterprise);
		Assertions.assertEquals(persistedEnterprise.getName(), savedEnterprise.getName());
		Assertions.assertEquals(persistedEnterprise.getFoundationDate(), savedEnterprise.getFoundationDate());
	}
	@Test
	@DisplayName("Should throws IllegalArgumentException when a Enterprise name exists")
	public void testCreateEnterpriseThrowsIllegalArgumentExceptionWhenNameExists() throws CloneNotSupportedException  {
		InsertEnterpriseDTO dto = new InsertEnterpriseDTO("Sample", LocalDate.of(1999, 1, 1));
		Enterprise enterprise = new Enterprise(dto);
		Enterprise persistedEnterprise = enterprise.clone();
		persistedEnterprise.setId(1L);
		when(repository.existsByName(dto.name())).thenReturn(true);
		when(repository.save(enterprise)).thenReturn(persistedEnterprise);
		
		Assertions.assertThrows(IllegalArgumentException.class, () -> service.createEnterprise(dto));
		
	}
	@Test
	@DisplayName("Shouldn't throw exception when a valid enterprise is provided")
	public void testValidateCreateEnterpriseDontThrowAnyException() {
		InsertEnterpriseDTO dto = new InsertEnterpriseDTO("Sample", LocalDate.of(1999, 1, 1));
		Enterprise enterprise = new Enterprise(dto);
		
		when(repository.existsByName(enterprise.getName())).thenReturn(false);
		
		Assertions.assertDoesNotThrow(()-> service.validateCreateEnterprise(enterprise));
	}
	
	@Test
	@DisplayName("Shouldn throw IllegalArgumentException when a existing enterprise name is provided")
	public void testValidateCreateEnterpriseThrowIllegalArgumentException() {
		InsertEnterpriseDTO dto = new InsertEnterpriseDTO("Sample", LocalDate.of(1999, 1, 1));
		Enterprise enterprise = new Enterprise(dto);
		
		when(repository.existsByName(enterprise.getName())).thenReturn(true);
		
		Assertions.assertThrows(IllegalArgumentException.class,()-> service.validateCreateEnterprise(enterprise));
	}
	
	@Test
	@DisplayName("Should return DefaultResponseEnterpriseDTO List")
	public void testFindAll() {
		Enterprise enterprise = new Enterprise(1L, "Sample", LocalDate.of(2000, 1, 1));
		DefaultResponseEnterpriseDTO dto = (DefaultResponseEnterpriseDTO) EnterpriseDTOFactory.createDTO(enterprise, TypeDTO.DEFAULT);
		when(repository.findAll()).thenReturn(List.of(enterprise));
		
		Assertions.assertEquals(List.of(dto), service.findAll());
	}
	
	@Test
	@DisplayName("Should return a present Optional with a existing id")
	public void testFindByIDReturnPresentOptional() {
		Long id = 1L;
		Enterprise enterprise = new Enterprise(id, "Sample", LocalDate.of(2000, 1, 1));
		when(repository.findById(id)).thenReturn(Optional.of(enterprise));
		
		Assertions.assertTrue(service.findById(id).isPresent());
	}
	@Test
	@DisplayName("Should return a empty Optional with a non existing ID")
	public void testFindByIDReturnEmptyOptional() {
		Long id = -1L;
		when(repository.findById(id)).thenReturn(Optional.empty());
		
		Assertions.assertTrue(!service.findById(id).isPresent());
	}
	
	@Test
	@DisplayName("Should update a Enterprise")
	public void testUpdate() throws CloneNotSupportedException {
		Enterprise beforeUpdate = new Enterprise(1L, "Sample", LocalDate.of(1999, 1, 1));
		String expectedUpdatedName = "Updated Sample";
		LocalDate expectedUpdatedFoundationDate = LocalDate.of(1997, 2, 2);
		UpdateEnterpriseDTO dto = new UpdateEnterpriseDTO(1L, expectedUpdatedName, expectedUpdatedFoundationDate);
		
		Enterprise afterUpdate = beforeUpdate.clone();
		afterUpdate.update(dto);
		
		when(repository.findById(1L)).thenReturn(Optional.of(beforeUpdate));
		when(repository.save(afterUpdate)).thenReturn(afterUpdate);
		
		Enterprise actualAfterUpdate = service.update(dto);
		Assertions.assertEquals(afterUpdate, actualAfterUpdate);
		Assertions.assertEquals(expectedUpdatedName, actualAfterUpdate.getName());
		Assertions.assertEquals(expectedUpdatedFoundationDate, actualAfterUpdate.getFoundationDate());
	}
	
	@Test
	@DisplayName("Should return null when a non existing ID is provided")
	public void testUpdateReturnNullWhenANonExistingIDIsProvided() throws CloneNotSupportedException {
		Enterprise beforeUpdate = new Enterprise(1L, "Sample", LocalDate.of(1999, 1, 1));
		String expectedUpdatedName = "Updated Sample";
		LocalDate expectedUpdatedFoundationDate = LocalDate.of(1997, 2, 2);
		UpdateEnterpriseDTO dto = new UpdateEnterpriseDTO(-1L, expectedUpdatedName, expectedUpdatedFoundationDate);
		
		Enterprise afterUpdate = beforeUpdate.clone();
		afterUpdate.update(dto);
		
		when(repository.findById(-1L)).thenReturn(Optional.empty());
		when(repository.save(afterUpdate)).thenReturn(afterUpdate);
		
		Assertions.assertNull(service.update(dto));
	}
	
	@Test
	@DisplayName("Should don't update a Enterprise when a empty name is provided")
	public void testUpdateDontChangeEnterpriseWhenEmptyNameIsProvided() throws CloneNotSupportedException {
		Enterprise beforeUpdate = new Enterprise(1L, "Sample", LocalDate.of(1999, 1, 1));
		String expectedUpdatedName = "Sample";
		LocalDate expectedUpdatedFoundationDate = LocalDate.of(1997, 2, 2);
		UpdateEnterpriseDTO dto = new UpdateEnterpriseDTO(1L, "", expectedUpdatedFoundationDate);
		
		Enterprise afterUpdate = beforeUpdate.clone();
		
		when(repository.findById(1L)).thenReturn(Optional.of(beforeUpdate));
		when(repository.save(afterUpdate)).thenReturn(afterUpdate);
		
		Enterprise actualAfterUpdate = service.update(dto);
		Assertions.assertEquals(afterUpdate, actualAfterUpdate);
		Assertions.assertEquals(expectedUpdatedName, actualAfterUpdate.getName());
	}
	
	@Test
	@DisplayName("Should delete a Enterprise")
	public void testDelete() {
		Long id = 1L;
		when(repository.existsById(id)).thenReturn(true);
		Assertions.assertTrue(service.delete(id));
		
	}
	@Test
	@DisplayName("Should return False when a Enterprise ID don't exists")
	public void testDeleteReturnFalse() {
		Long id = -1L;
		when(repository.existsById(id)).thenReturn(false);
		Assertions.assertTrue(!service.delete(id));
		
	}
	
	@Test
	@DisplayName("Should throw a exception when there's games referenced to Enteprise that will be deleted")
	public void testDeleteThrowAException() {
		Long id = 1L;
		when(repository.existsById(id)).thenReturn(true);
		doThrow(DataIntegrityViolationException.class).when(repository).deleteById(id);;
		
		Assertions.assertThrows(DataIntegrityViolationException.class, () -> service.delete(id));
	}
}
