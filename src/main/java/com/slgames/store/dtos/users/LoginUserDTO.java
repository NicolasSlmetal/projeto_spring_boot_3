package com.slgames.store.dtos.users;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginUserDTO(
		String nickname, 
		@Email String email, 
		@NotBlank String password) {

}
