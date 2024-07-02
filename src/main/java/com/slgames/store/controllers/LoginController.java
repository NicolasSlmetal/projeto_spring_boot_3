package com.slgames.store.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.slgames.store.dtos.users.LoginUserDTO;
import com.slgames.store.infra.TokenService;
import com.slgames.store.infra.TokenService.TokenDTO;
import com.slgames.store.model.User;
import com.slgames.store.model.services.LoginService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;

@RestController
@RequestMapping("/login")
@Getter
@Tag(name = "/login", description = "This is the endpoint to authenticate users.")
public class LoginController {

	@Autowired
	private  AuthenticationManager manager;
	
	@Autowired
	private LoginService service;
	
	@Autowired
	private TokenService tokenService;
	
	@Operation(summary = "Make the login of the user based on your nickname/email and password.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description="The user has been authenticated."),
			@ApiResponse(responseCode = "403", description="Some credential is wrong.")
	})
	@PostMapping
	public ResponseEntity<TokenDTO> login(@RequestBody LoginUserDTO dto){
		service.validateNicknameOrEmailPresent(dto);
		String nickname = service.getDefaultNickname(dto);
		Authentication loginRequest = UsernamePasswordAuthenticationToken
				.unauthenticated(nickname, dto.password());
		Authentication authenticationResponse = manager.authenticate(loginRequest);
		TokenDTO tokenResponse = tokenService.generateTokenForAuthenticatedUser((User) authenticationResponse.getPrincipal());
		return ResponseEntity.ok(tokenResponse);
		
	}
	
}
