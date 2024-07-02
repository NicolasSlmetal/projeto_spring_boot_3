package com.slgames.store.dtos.users;

import java.time.LocalDateTime;

import com.slgames.store.dtos.DTO;

public record UpdatedUserResponseDTO(
		String username, 
		String token, 
		LocalDateTime tokenExpiration) implements DTO {
	
}
