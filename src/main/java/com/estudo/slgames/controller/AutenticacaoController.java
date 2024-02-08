package com.estudo.slgames.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.estudo.slgames.dtos.AuthDTO;
import com.estudo.slgames.dtos.DadosTokenDTO;
import com.estudo.slgames.infra.TokenProviderService;
import com.estudo.slgames.model.auth.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/login")
@Tag(name="Login", description = "Obtenção do token de acesso")

public class AutenticacaoController {
	
	
	@Autowired
	private TokenProviderService tokenProvider;
	
	@Autowired
	private AuthenticationManager manager;
	@Operation(summary = "Autenticação do Usuário", method="POST", tags="Login")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", content=@Content, description="Autenticação bem-sucedida e envio do token"),
			@ApiResponse(responseCode = "403", content=@Content, description="Credenciais inválidas"), 
			@ApiResponse(responseCode = "500", content=@Content, description="Erro ao autenticar")
	})
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> efetuarLogin(@RequestBody @Valid AuthDTO body){
		var token = new UsernamePasswordAuthenticationToken(body.login(), body.senha());
		var auth = manager.authenticate(token);
		
		var tokenJWT = tokenProvider.gerarToken((User) auth.getPrincipal());
		return ResponseEntity.ok(new DadosTokenDTO(tokenJWT));
	}
	
}
