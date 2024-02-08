package com.estudo.slgames.infra;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class HandlerDeErros {

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<?> tratar404() {
		return ResponseEntity.notFound().build();
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> tratar400(MethodArgumentNotValidException ex){
		var erros = ex.getFieldErrors();
		return ResponseEntity.badRequest().body(erros.stream().map(Erros::new).toList());
	}
	
	
	public record Erros(String message, String field) {
		public Erros(FieldError erro) {
			this(erro.getField(), erro.getDefaultMessage());
		}
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<?> invalidarParametros(){
		return ResponseEntity.badRequest().build();
	}
}

