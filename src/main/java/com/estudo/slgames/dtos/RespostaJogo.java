package com.estudo.slgames.dtos;

import java.time.LocalDate;

import com.estudo.slgames.model.Jogo;


public record RespostaJogo(
		Long id,
		String nome,
		Double preco,
		String dev, 
		String pub, 
		Genero genero,
		LocalDate data, 
		boolean disponivel
		) {

	
	public RespostaJogo(Jogo jogo) {
		this(
				jogo.getId(),
				jogo.getNome(), 
				jogo.getPreco(), 
				jogo.getDev(),
				jogo.getPub(), 
				jogo.getGenero(), 
				jogo.getData(), 
				jogo.isDisponivel());
		
	}
}
