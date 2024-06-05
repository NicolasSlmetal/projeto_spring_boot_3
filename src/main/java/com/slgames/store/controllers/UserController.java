package com.slgames.store.controllers;

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

import com.slgames.store.dtos.TypeDTO;
import com.slgames.store.dtos.enterprise.CreatedResponseEnterpriseDTO;
import com.slgames.store.dtos.users.CreatedResponseUserDTO;
import com.slgames.store.dtos.users.DefaultResponseUserDTO;
import com.slgames.store.dtos.users.InsertUserDTO;
import com.slgames.store.dtos.users.UpdateUserDTO;
import com.slgames.store.dtos.users.UserDTOFactory;
import com.slgames.store.model.User;
import com.slgames.store.model.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.Getter;

@RestController
@RequestMapping("/user")
@Getter
@Tag(name = "/user", description = "This is the endpoint to manipulate and acess users data.")
public class UserController {

	@Autowired
	private UserService service;
	
	@GetMapping
	@Operation(summary = "Return all users stored on the API, showing a Default DTO object of them.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Show all users data stored.")
	})
	public ResponseEntity<List<DefaultResponseUserDTO>> findAll() {
		return ResponseEntity.ok(getService().findAll());
	}
	
	@GetMapping("/{id}")
	@Operation(summary = "Return a specific user based on your ID. If not exists, will return HTTP status 404. Note that the sensive data isn't showed.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Show the user data."),
			@ApiResponse(responseCode = "404", description = "Not found.")
	})
	
	//Different of others controllers because we need to hide sensitive data as password
	public ResponseEntity<DefaultResponseUserDTO> findById(@PathVariable Long id){
		Optional<User> user = getService().findById(id);
		if (user.isPresent()) return ResponseEntity.ok((DefaultResponseUserDTO) 
				UserDTOFactory
				.createDTO(user.get(), TypeDTO.DEFAULT));
		else return ResponseEntity.notFound().build();
	
	}
	
	@PostMapping
	@Transactional
	@Operation(summary = "Insert a new user in the database.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description="The user is created in the database"),
			@ApiResponse(responseCode = "400", description="User data isn't allowed. Some reasons are: "
					+ "\n - Empty email and/or password;"
					+ "\n - Existing email is provided.")
	})
	public ResponseEntity<CreatedResponseUserDTO> insertUser(@RequestBody @Valid InsertUserDTO dto, UriComponentsBuilder builder){
		User user = getService().createUser(dto);
		var uri = builder.path("/user/{id}").buildAndExpand(user.getId()).toUri();
		return ResponseEntity.created(uri).body(
				(CreatedResponseUserDTO) UserDTOFactory.
				createDTO(user, TypeDTO.CREATED));
	}
	
	@PutMapping
	@Transactional
	@Operation(summary = "Update the data of a user.")
	@ApiResponses(value = {
			@ApiResponse(responseCode="200", description="The user data was updated."), 
			@ApiResponse(responseCode="400", description="Could occur when Id is not provided."),
			@ApiResponse(responseCode="404", description="The ID was not found in the database."),
			@ApiResponse(responseCode="500", description="Could occur when email/nickname/password is updated with a existing value.")
	})
	public ResponseEntity<DefaultResponseUserDTO> updateUser(@RequestBody UpdateUserDTO dto){
		User user = getService().updateUser(dto);
		if (user != null) {
			return ResponseEntity
					.ok( (DefaultResponseUserDTO)
							UserDTOFactory
							.createDTO(user, TypeDTO.DEFAULT));
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@DeleteMapping("/{id}")
	@Transactional
	@Operation(summary = "Delete a user based on your Id.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "The user was deleted sucessfuly."),
			@ApiResponse(responseCode = "404", description = "The user doesn't exist.")
	})
	public ResponseEntity<?> deleteUser(@PathVariable Long id){
		if (getService().deleteUser(id)) {
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
	
	
}
