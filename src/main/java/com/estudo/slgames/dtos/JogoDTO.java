package com.estudo.slgames.dtos;

import java.time.LocalDate;

import com.estudo.slgames.model.Jogo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

public record JogoDTO(
		@NotBlank String nome, 
		@NotNull Double preco,
		@NotBlank String dev, 
		@NotBlank String pub, 
		@NotNull Genero genero, 
		@PastOrPresent @NotNull LocalDate data) {

	
	public JogoDTO(Jogo jogo) {
		this(jogo.getNome(), jogo.getPreco(), jogo.getDev(), jogo.getPub(), jogo.getGenero(), jogo.getData());
	}
}
