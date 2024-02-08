package com.estudo.slgames.dtos;

import jakarta.validation.constraints.NotBlank;

public record AuthDTO(
		@NotBlank String login,
		@NotBlank String senha) {

}
