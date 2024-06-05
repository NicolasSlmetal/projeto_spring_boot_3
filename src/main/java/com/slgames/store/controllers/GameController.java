package com.slgames.store.controllers;



import java.util.List;

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


import com.slgames.store.dtos.game.CreatedResponseGameDTO;
import com.slgames.store.dtos.game.DefaultResponseGameDTO;
import com.slgames.store.dtos.game.GameDTOFactory;
import com.slgames.store.dtos.game.InsertGameDTO;
import com.slgames.store.dtos.game.UpdateGameDTO;
import com.slgames.store.model.Game;
import com.slgames.store.model.services.GameService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.Getter;

@RestController
@RequestMapping("/game")
@Getter
@Tag(name = "/game", description = "This is the endpoint to manipulate and acess games data.")
public class GameController {
	
	
	@Autowired
	private GameService service;
	@Operation(summary = "Return all games stored on the API, showing a Default DTO object of them.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Show all games data stored.")
	})
	
	@GetMapping
	public ResponseEntity<List<DefaultResponseGameDTO>> findAllGames(){
		return ResponseEntity.ok(getService().findAll());
	}
	@Operation(summary = "Return a specific game based on your ID. If not exists, will return HTTP status 404.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Show the game data."),
			@ApiResponse(responseCode = "404", description = "Not found.")
	})
	@GetMapping("/{id}")
	public ResponseEntity<Game> findGameById(@PathVariable Long id){
		return ResponseEntity.of(getService().findById(id));
	}
	
	@PostMapping
	@Transactional
	@Operation(summary = "Insert a new game in the database.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description= "The game data provided is created." ),
			@ApiResponse(responseCode = "400", description= "The game data is not allowed. The possible reasons to it is: "
					+ "\n - Name provided already exists;"
					+ "\n- Devloper id doesn't exist;"
					+ "\n- Publisher id doesn't exist;"
					+ "\n- Price is negative;"
					+ "\n - Some Genre is wrong;"
					+ "\n - The date is in the future.")
	})
	public ResponseEntity<CreatedResponseGameDTO> insertGame(@RequestBody @Valid InsertGameDTO gameDto, UriComponentsBuilder builder){
			Game game = getService().createGame(gameDto);
			var uri = builder.path("/game/{id}").buildAndExpand(game.getId()).toUri();
			return ResponseEntity.created(uri)
					.body((CreatedResponseGameDTO)
					GameDTOFactory
					.getInstance()
					.fabricateCreated(game));
	}
	@PutMapping
	@Transactional
	@Operation(summary = "Update the data of a game.")
	@ApiResponses(value = {
			@ApiResponse(responseCode="200", description="The game data was updated."), 
			@ApiResponse(responseCode="400", description="Could occur when Id is not provided."),
			@ApiResponse(responseCode="404", description="The ID was not found in the database.")
	})
	public ResponseEntity<DefaultResponseGameDTO> updateGame(@RequestBody @Valid UpdateGameDTO gameDto){
		Game game = getService().update(gameDto);
		if (game != null) {
			return ResponseEntity.ok().body(new DefaultResponseGameDTO(game));
		}else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@DeleteMapping("/{id}")
	@Transactional
	@Operation(summary = "Delete a game based on your Id.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "The game was deleted sucessfuly."),
			@ApiResponse(responseCode = "404", description = "The game doesn't exist.")
	})
	public ResponseEntity<?> deleteGame(@PathVariable Long id){
		if (getService().delete(id)) return ResponseEntity.noContent().build();
		else return ResponseEntity.notFound().build();
	}
}