package com.slgames.store.dtos.users;

import com.slgames.store.dtos.AbstractDTOFactory;
import com.slgames.store.dtos.CreatedDTO;
import com.slgames.store.dtos.DTO;
import com.slgames.store.dtos.DefaultDTO;
import com.slgames.store.dtos.TypeDTO;
import com.slgames.store.infra.TokenService.TokenDTO;
import com.slgames.store.model.User;


public class UserDTOFactory implements AbstractDTOFactory<User>{

	private static UserDTOFactory INSTANCE = new UserDTOFactory();
	
	public static UserDTOFactory getInstance() {
		if (INSTANCE == null) INSTANCE = new UserDTOFactory();
		return INSTANCE;
	}
	
	public static DTO createDTO(User user, TypeDTO type) {
		switch (type) {
			case CREATED:
				return getInstance().fabricateCreated(user);
			case DEFAULT:
				return getInstance().fabricateDefault(user);
			case UPDATED:
			default:
				return null;
		}
	}
	@Override
	public CreatedDTO fabricateCreated(User object) {
		return new CreatedResponseUserDTO(object);
	}

	@Override
	public DefaultDTO fabricateDefault(User object) {
		return new DefaultResponseUserDTO(object);
	}
	
	public UpdatedUserResponseDTO fabricateUpdatedUser(User user, TokenDTO tokenDto) {
		return new UpdatedUserResponseDTO(user.getUsername(), 
				tokenDto.token(), 
				tokenDto.expiration());
	}

}
