package com.estudo.slgames.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.estudo.slgames.dtos.AtualizarJogo;
import com.estudo.slgames.dtos.JogoDTO;
import com.estudo.slgames.dtos.RespostaJogo;
import com.estudo.slgames.model.Jogo;
import com.estudo.slgames.model.JogoRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/jogo")
@Tag(name = "open-api")
public class JogoController {
	
	@Autowired
	private JogoRepository repository;
	
	@Operation(summary = "Obtém todos os jogos cadastrados disponíveis", method="GET")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Envio de todos os jogos cadastrados"),
			@ApiResponse(responseCode = "403", description = "Token não enviado ou inválido")
	})
	@GetMapping
	public ResponseEntity<List<RespostaJogo>> listar(){
		var lista = repository.findAllByDisponivelTrue().stream().map(RespostaJogo::new).toList();
		return ResponseEntity.ok(lista);
	}
	@Operation(summary = "Obtém um jogo cadastrado por Id", method="GET")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Envio do jogo por id"),
			@ApiResponse(responseCode = "403", description = "Token não enviado ou inválido"),
			@ApiResponse(responseCode = "404", description = "Não encontrado")
	})
	@GetMapping("/{id}")
	public ResponseEntity<RespostaJogo> encontrePorId(@PathVariable Long id) {
		var jogo = repository.getReferenceById(id);
		return ResponseEntity.ofNullable(new RespostaJogo(jogo));
	}
	@Operation(summary = "Cadastra um jogo", method="POST")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Jogo cadastrado"),
			@ApiResponse(responseCode = "400", description = "Parâmetro(s) inválido(s)"),
			@ApiResponse(responseCode = "403", description = "Token não enviado ou inválido"),
	})
	@PostMapping
	@Transactional
	public ResponseEntity<RespostaJogo> inserir(@RequestBody @Valid JogoDTO dto, UriComponentsBuilder uriBuilder) {
		var jogo = new Jogo(dto);
		repository.save(jogo);
		
		var uri = uriBuilder.path("/jogo/{id}").buildAndExpand(jogo.getId()).toUri();
		
		return ResponseEntity.created(uri).body(new RespostaJogo(jogo));
	}
	@Operation(summary = "Atualiza um jogo. Só é possível atualizar nome e preço", method="PUT")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Informações do jogo atualizadas"),
			@ApiResponse(responseCode = "400", description = "Parâmetro(s) inválido(s)"),
			@ApiResponse(responseCode = "403", description = "Token não enviado ou inválido"),
			@ApiResponse(responseCode = "404", description = "Não encontrado")
	})
	@PutMapping
	@Transactional
	public ResponseEntity<RespostaJogo> atualizar(@RequestBody @Valid AtualizarJogo body) {
		Optional<Jogo> jogo = repository.findById(body.id());
		if (jogo.isPresent()) {
			Jogo jogoAtualizado = jogo.get();
			jogoAtualizado.atualizarPorDTO(body);
			repository.save(jogoAtualizado);
			return ResponseEntity.ok(new RespostaJogo(jogoAtualizado));
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	@Operation(summary = "Ativa um jogo indisponível", method="PUT")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Informações do jogo atualizadas"),
			@ApiResponse(responseCode = "403", description = "Token não enviado ou inválido"),
			@ApiResponse(responseCode = "404", description = "Não encontrado")
	})
	@PutMapping("ativar/{id}")
	@Transactional
	public ResponseEntity<Void> ativar(@PathVariable Long id) {
		Optional<Jogo> jogo = repository.findById(id);
		if (jogo.isPresent()) {
			jogo.get().setDisponivel(true);
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	@Operation(summary = "Deleta um jogo", method="DELETE")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Jogo deletado"),
			@ApiResponse(responseCode = "403", description = "Token não enviado ou inválido"),
			@ApiResponse(responseCode = "404", description = "Não encontrado")
	})
	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity<Void> deletar(@PathVariable Long id) {
		if (repository.existsById(id)) {
			repository.deleteById(id);
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	@Operation(summary = "Torna um jogo indisponível", method="DELETE")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Jogo inativado"),
			@ApiResponse(responseCode = "403", description = "Token não enviado ou inválido"),
			@ApiResponse(responseCode = "404", description = "Não encontrado")
	})
	@DeleteMapping("inativar/{id}")
	@Transactional
	public ResponseEntity<Void> inativar(@PathVariable Long id) {
		Optional<Jogo> jogo = repository.findById(id);
		if (jogo.isPresent()) { 
			jogo.get().setDisponivel(false);
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}
