package com.slgames.store.infra;

import java.util.Arrays;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.slgames.store.model.GenreName;



@RestControllerAdvice
public class ErrorHandler {

	
	private static String PATTERN_GENRENAME_PARSE_ERROR = "JSON parse error: Cannot deserialize value of type `com.slgames.store.model.GenreName` from String \"SURVIVAL\": not one of the values accepted for Enum class: [RACE, TERROR, ACTION, RPG, PUZZLE, FPS, SCI_FI]";
	private static String MESSAGE_ENTERPRISE_REFERENCE_GAMES = "could not execute statement [Cannot delete or update a parent row: a foreign key constraint fails (`slgames`.`games`, CONSTRAINT `games_ibfk_1` FOREIGN KEY (`developer`) REFERENCES `enterprises` (`id`))] [delete from enterprises where id=?]; SQL [delete from enterprises where id=?]; constraint [null]";
	private static String DUPLICATED_VALUE_MESSAGE = "could not execute statement [Duplicate entry ! for key 'enterprises.name_enterprise'] [update enterprises set foundation_date=?,name_enterprise=? where id=?]; SQL [update enterprises set foundation_date=?,name_enterprise=? where id=?]; constraint [enterprises.name_enterprise]";
	
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<?> retriveCannotDelete(DataIntegrityViolationException ex){
		String mes = ex.getLocalizedMessage();
		String[] splitMessage = DUPLICATED_VALUE_MESSAGE.split("!");
		if (mes.contains(MESSAGE_ENTERPRISE_REFERENCE_GAMES)) {
			mes = "Cannot delete enterprise because there are games referenced to it";
		} else if (mes.contains(splitMessage[0].strip()) && mes.contains(splitMessage[1].strip())){
			mes = "Cannot update enterprise name with the provided value, because the name already exists.";
		} else if (mes.contains("users")) mes = "Some data provided already exist on database";
		return ResponseEntity.internalServerError().body(new ExceptionBody(mes));
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<?> invalidateParameter(IllegalArgumentException illegalEx){
		String mes = buildMessage(illegalEx.getMessage());
		return ResponseEntity.badRequest().body(new ExceptionBody(mes));
	}
	@ExceptionHandler(InternalAuthenticationServiceException.class)
	public ResponseEntity<?> returnForbidden(InternalAuthenticationServiceException authenticationException){
		
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
	}
	
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<?> invalidateCredentialsWithForbidden(){
		return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); 
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<?> invalidateEnumValue(HttpMessageNotReadableException httpEx){
		String mes = buildMessage(httpEx.getMessage());
		return ResponseEntity.badRequest().body(new ExceptionBody(mes));
	}
	
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<?> verifyRuntimeException(RuntimeException runtimeEx){
		String mes = buildMessage(runtimeEx.getMessage());
		return ResponseEntity.internalServerError().body(new ExceptionBody(mes));
	}

	private String buildMessage(String mes) {
		if (mes.contains(PATTERN_GENRENAME_PARSE_ERROR)) {
			mes = "No such Genre. Value should be in %s".formatted(Arrays.toString(GenreName.values()));
		}
		return mes;
	}
	
	record ExceptionBody(String message){
		ExceptionBody() {
			this("Some unreconigzed error has occured");
		}
	}
}
