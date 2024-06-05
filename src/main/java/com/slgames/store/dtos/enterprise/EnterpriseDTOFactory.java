package com.slgames.store.dtos.enterprise;

import com.slgames.store.dtos.AbstractDTOFactory;
import com.slgames.store.dtos.CreatedDTO;
import com.slgames.store.dtos.DTO;
import com.slgames.store.dtos.DefaultDTO;
import com.slgames.store.dtos.TypeDTO;
import com.slgames.store.model.Enterprise;

public class EnterpriseDTOFactory implements AbstractDTOFactory<Enterprise> {
	
	public static EnterpriseDTOFactory INSTANCE;
	
	public static DTO createDTO(Enterprise enterprise, TypeDTO type) {
		switch (type) {
			case TypeDTO.CREATED:
				return getInstance().fabricateCreated(enterprise);
			case TypeDTO.DEFAULT:
				return getInstance().fabricateDefault(enterprise);
			default: 
				return null;
		
		}
	}
	
	public synchronized static EnterpriseDTOFactory getInstance() {
		if (INSTANCE == null) INSTANCE = new EnterpriseDTOFactory();
		return INSTANCE;
	}
	

	@Override
	public CreatedDTO fabricateCreated(Enterprise object) {
		return new CreatedResponseEnterpriseDTO(object);
	}

	@Override
	public DefaultDTO fabricateDefault(Enterprise object) {
		return new DefaultResponseEnterpriseDTO(object);
	}
	
	

}
