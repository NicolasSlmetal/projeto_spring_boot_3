package com.estudo.slgames.model;

import java.time.LocalDate;

import com.estudo.slgames.dtos.AtualizarJogo;
import com.estudo.slgames.dtos.Genero;
import com.estudo.slgames.dtos.JogoDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity(name = "jogos")
@Table(name = "jogos")
public class Jogo {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nome;
	private Double preco;
	private String dev;
	private String pub;
	@Enumerated(EnumType.STRING)
	private Genero genero;
	private LocalDate data;
	private boolean disponivel;
	
	
	
	public Jogo(JogoDTO dto) {
		setNome(dto.nome());
		setPreco(dto.preco());
		setDev(dto.dev());
		setPub(dto.pub());
		setGenero(dto.genero());
		setData(dto.data());
		setDisponivel(true);
	}



	public void atualizarPorDTO(@Valid AtualizarJogo body) {
		if (body.nome() != null && !body.nome().isBlank()) setNome(body.nome());
		if (body.preco() != null) setPreco(body.preco());
	}
	
	
}
