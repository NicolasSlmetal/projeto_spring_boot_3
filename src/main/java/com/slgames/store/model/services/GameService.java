package com.slgames.store.model.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.slgames.store.dtos.TypeDTO;
import com.slgames.store.dtos.game.DefaultResponseGameDTO;
import com.slgames.store.dtos.game.GameDTOFactory;
import com.slgames.store.dtos.game.InsertGameDTO;
import com.slgames.store.dtos.game.UpdateGameDTO;
import com.slgames.store.model.Game;
import com.slgames.store.model.repository.GameRepository;

import lombok.Getter;

@Service
@Getter
public class GameService {
	
	private final static double FREE_GAME_PRICE = 0.00;
	
	@Autowired
	private GameRepository repository;
	
	@Autowired
	private GenreService genreService;
	
	@Autowired
	private EnterpriseService enterpriseService;
	
	public boolean isPositiveOrFree(Double price) {
		return price >= FREE_GAME_PRICE;
	}
	
	public boolean existsByTitle(String title) {
		return getRepository().existsByTitle(title);
	}
	
	public boolean existsById(Long id) {
		return getRepository().existsById(id);
	}
	
	public boolean existsEnterprise(Long id) {
		return getEnterpriseService().existsById(id);
	}
	
	public List<DefaultResponseGameDTO> findAll(){
		return getRepository().findAll().stream().map(game -> (DefaultResponseGameDTO) 
				GameDTOFactory.createDTO(game, TypeDTO.DEFAULT)).toList();
	}
	
	public Optional<Game> findById(Long id){
		return getRepository().findById(id);
	}
	
	
	public Game createGame(InsertGameDTO gameDto) {
		Game game = new Game(gameDto);
		game.getGenres().forEach(
				genre -> genre.setId(getGenreService().getIdByGenreName(genre.getGenreName())));
		validateNewGame(game);
		updateDevAndPublisher(game);
		return getRepository().save(game);
	}

	public void updateDevAndPublisher(Game game) {
		game.setDeveloper(getEnterpriseService().findById(game.getDeveloperId()).get());
		game.setPublisher(getEnterpriseService().findById(game.getPublisherId()).get());
	}

	public void validateNewGame(Game game)  {
		if (existsByTitle(game.getTitle())) throw new IllegalArgumentException("Title '%s' already exists on table".formatted(game.getTitle()));
		if (!isPositiveOrFree(game.getPrice())) throw new IllegalArgumentException("Price must be higher or equal than 0.00");
		if (!existsEnterprise(game.getDeveloperId())) throw new IllegalArgumentException(String.format("No existing id %d, for Developer", game.getDeveloperId()));
		if (!existsEnterprise(game.getPublisherId())) throw new IllegalArgumentException(String.format("No existing id %d, for Publisher", game.getPublisherId()));
		
	}

	
	
	
	public Game update(UpdateGameDTO gameDto) {
		Optional<Game> opGame = findById(gameDto.id());
		if (opGame.isPresent()) {
			Game game = opGame.get();
			game.update(gameDto);
			validateUpdate(game);
			return getRepository().save(game);
		}else {
			return null;
		}
	}

	public void validateUpdate(Game game) {
		if (!isPositiveOrFree(game.getPrice())) throw new IllegalArgumentException("Price must be higher or equal than 0.00");
	}
	
	public boolean delete(Long id) {
		if (findById(id).isPresent()) {
			getRepository().deleteById(id);
			return true;
		}else return false;
	}
}
