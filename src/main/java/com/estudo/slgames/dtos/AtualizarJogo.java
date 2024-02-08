package com.estudo.slgames.dtos;

import jakarta.validation.constraints.NotNull;

public record AtualizarJogo(
		@NotNull Long id, 
		String nome, 
		Double preco
		) {

}
