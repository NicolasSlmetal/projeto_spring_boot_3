package com.slgames.store.model.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.slgames.store.dtos.TypeDTO;
import com.slgames.store.dtos.users.DefaultResponseUserDTO;
import com.slgames.store.dtos.users.InsertUserDTO;
import com.slgames.store.dtos.users.UpdateUserDTO;
import com.slgames.store.dtos.users.UpdatedUserResponseDTO;
import com.slgames.store.dtos.users.UserDTOFactory;
import com.slgames.store.infra.TokenService;
import com.slgames.store.infra.TokenService.TokenDTO;
import com.slgames.store.model.User;
import com.slgames.store.model.repository.UserRepository;

import lombok.Getter;

@Service
@Getter
public class UserService {

	@Autowired
	private UserRepository repository;
	
	@Autowired
	private TokenService tokenService;
	
	public List<DefaultResponseUserDTO> findAll(){
		return getRepository().findAll().stream()
				.map(user -> (DefaultResponseUserDTO) 
						UserDTOFactory
						.createDTO(user, TypeDTO.DEFAULT))
				.toList();
	}
	
	public Optional<User> findById(Long id){
		return getRepository().findById(id);
	}
	
	
	
	public User createUser(InsertUserDTO dto) {
		User user = new User(dto);
		validateUserInformation(user);
		encriptPassword(user);
		return getRepository().save(user);
	}

	private void validateUserInformation(User user) {
		if (getRepository().existsByEmail(user.getEmail()) || 
				getRepository().existsByNickname(user.getNickname()) ||
				getRepository().existsByPassword(user.getPassword())) throw new IllegalArgumentException("User data already exists on database.");
	}
	
	
	public void encriptPassword(User user) {
		String encriptedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
		user.setPassword(encriptedPassword);
	}
	

	public User updateUser(UpdateUserDTO dto) {
		if (getRepository().existsById(dto.id())) {
			User user = getRepository().findById(dto.id()).get();
			user.updateUser(dto);
			if (dto.password() != null && !dto.password().isBlank() && !dto.password().isEmpty()) encriptPassword(user);
			return getRepository().save(user);
		} else return null;
	}

	public boolean deleteUser(Long id) {
		if (getRepository().existsById(id)) {
			getRepository().deleteById(id);
			return true;
		} else return false;
		
	}
	
	public UpdatedUserResponseDTO refreshTokenToUpdatedUser(User user) {
		TokenDTO tokenDto = tokenService.generateTokenForAuthenticatedUser(user);
		return UserDTOFactory.getInstance().fabricateUpdatedUser(user, tokenDto);
	}
}
