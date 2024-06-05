package com.slgames.store.model.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slgames.store.dtos.TypeDTO;
import com.slgames.store.dtos.enterprise.DefaultResponseEnterpriseDTO;
import com.slgames.store.dtos.enterprise.EnterpriseDTOFactory;
import com.slgames.store.dtos.enterprise.InsertEnterpriseDTO;
import com.slgames.store.dtos.enterprise.UpdateEnterpriseDTO;
import com.slgames.store.model.Enterprise;
import com.slgames.store.model.repository.EnterpriseRepository;

import lombok.Getter;

@Service
@Getter
public class EnterpriseService {

	@Autowired
	private EnterpriseRepository repository;
	
	public boolean existsById(Long id) {
		return getRepository().existsById(id);
	}
	
	public boolean existsByName(String name) {
		return getRepository().existsByName(name);
	}
	
	public List<DefaultResponseEnterpriseDTO> findAll(){
		return getRepository().findAll().stream().map( enterprise -> (DefaultResponseEnterpriseDTO)
		EnterpriseDTOFactory.createDTO(enterprise, TypeDTO.DEFAULT)).toList();
	}
	
	public Optional<Enterprise> findById(Long id) {
		return getRepository().findById(id);
	}

	public void validateCreateEnterprise(Enterprise enterprise) {
		if (existsByName(enterprise.getName())) throw new IllegalArgumentException(String.format("Enterprise name '%s' already exists", enterprise.getName()));
	}
	
	public Enterprise createEnterprise(InsertEnterpriseDTO enterpriseDto) {
		Enterprise enterprise = new Enterprise(enterpriseDto);
		validateCreateEnterprise(enterprise);
		return getRepository().save(enterprise);
	}
	
	public Enterprise update(UpdateEnterpriseDTO enterpriseDto) {
		Optional<Enterprise> OpEnterprise = getRepository().findById(enterpriseDto.id());
		if (OpEnterprise.isPresent()) {
			Enterprise enterprise = OpEnterprise.get();
			enterprise.update(enterpriseDto);
			return getRepository().save(enterprise);
		} else {
			return null;
		}
	}

	public boolean delete(Long id) {
		if (existsById(id)) {
			getRepository().deleteById(id);
			return true;
		} else return false;
	}
}
