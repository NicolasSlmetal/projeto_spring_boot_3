package com.slgames.store.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.slgames.store.dtos.DTO;
import com.slgames.store.dtos.enterprise.CreatedResponseEnterpriseDTO;
import com.slgames.store.dtos.enterprise.DefaultResponseEnterpriseDTO;
import com.slgames.store.dtos.enterprise.InsertEnterpriseDTO;
import com.slgames.store.dtos.enterprise.UpdateEnterpriseDTO;
import com.slgames.store.dtos.game.CreatedResponseGameDTO;
import com.slgames.store.dtos.game.DefaultResponseGameDTO;
import com.slgames.store.dtos.game.InsertGameDTO;
import com.slgames.store.dtos.game.UpdateGameDTO;
import com.slgames.store.dtos.genre.GenreDTO;
import com.slgames.store.dtos.users.CreatedResponseUserDTO;
import com.slgames.store.dtos.users.DefaultResponseUserDTO;
import com.slgames.store.dtos.users.InsertUserDTO;
import com.slgames.store.dtos.users.LoginUserDTO;
import com.slgames.store.dtos.users.UpdateUserDTO;
import com.slgames.store.dtos.users.UpdatedUserResponseDTO;
import com.slgames.store.model.Enterprise;
import com.slgames.store.model.Game;
import com.slgames.store.model.Genre;
import com.slgames.store.model.GenreName;
import com.slgames.store.model.Role;
import com.slgames.store.model.Role.RoleName;
import com.slgames.store.model.User;

public class TestObjects {

	
	public static class GameTest implements BodyObjectTest{
		
		private Set<GenreDTO> genres;
		
		public GameTest() {
			randomizeGenres();
		}
		
		public Game createGame() {
			Set<Genre> convertedGenres = genres
					.stream()
					.map(dto -> new Genre(dto))
					.collect(Collectors.toSet());
			return Game.builder()
					.id(1L)
					.price(29.99)
					.launchDate(LocalDate.now().minusMonths(1))
					.genres(convertedGenres)
					.developer(new Enterprise(1L, "Inc 1", LocalDate.of(1999, 1, 1)))
					.publisher(new Enterprise(1L, "Inc 1", LocalDate.of(1999, 2, 1)))
					.build();
		}
		
	
		
		public void randomizeGenres() {
			genres = new HashSet<>();
			Random r = new Random();
			GenreName random = GenreName.values()[r.nextInt(GenreName.values().length)];
			genres.add(new GenreDTO(random));
		}

		@Override
		public DTO createdDTO() {
			
			return new InsertGameDTO("Sample", 
					LocalDate.now().minusMonths(1),
					29.99,
					1L, 
					1L,
					genres);
		}
		
		public DTO createdDTOWithBlankName() {
			return new InsertGameDTO("", 
					LocalDate.now().minusMonths(1),
					29.99,
					1L, 
					1L,
					genres);
		}
		
		public DTO createdDTOWithInvalidDeveloperId() {
			return new InsertGameDTO("Sample", 
					LocalDate.now().minusMonths(1),
					29.99,
					-1L, 
					1L,
					genres);
		}
		public DTO createdDTOWithInvalidPublisherId() {
			return new InsertGameDTO("Sample", 
					LocalDate.now().minusMonths(1),
					29.99,
					1L, 
					-1L,
					genres);
		}
		
		public DTO createdDTOWithNegativePrice() {
			return new InsertGameDTO("Sample", 
					LocalDate.now().minusMonths(1),
					-29.99,
					1L, 
					1L,
					genres);
		}

		@Override
		public DTO updatedDTO() {
			return new UpdateGameDTO
					(1L, 
					"Sample",
					null,
					55.98);
		}

		@Override
		public DTO updatedDTOWithInvalidID() {
			return new UpdateGameDTO
					(-1L, 
					"Sample",
					LocalDate.now().minusMonths(1),
					55.88);
		}
		public DTO updatedDTOWithNullID() {
			return new UpdateGameDTO
					(null, 
					"Sample",
					LocalDate.now().minusMonths(1),
					55.88);
		}

		@Override
		public DTO expectedDTO() {
			return new DefaultResponseGameDTO(
					1L,
					"Sample",
					LocalDate.now().minusMonths(1),
					29.99,
					genres);
		}
		public DTO expectedDTO(String title) {
			return new DefaultResponseGameDTO(
					1L,
					title,
					LocalDate.now().minusMonths(1),
					29.99,
					genres);
		}
		public DTO expectedDTO(String title, Double price) {
			return new DefaultResponseGameDTO(
					1L,
					title,
					LocalDate.now().minusMonths(1),
					price,
					genres);
		}

		@Override
		public DTO expectedCreatedDTO() {
			return new CreatedResponseGameDTO(1L, "Sample");
		}
		
		
	}
	
	public static class EnterpriseTest implements BodyObjectTest{

		public Enterprise createEnterprise() {
			return new Enterprise(1L, 
					"Sample", 
					LocalDate.of(1999, 1, 1));
		}
		
		@Override
		public DTO createdDTO() {
			return new InsertEnterpriseDTO("Sample", LocalDate.of(1999, 1, 1));
		}

		@Override
		public DTO expectedCreatedDTO() {
			return new CreatedResponseEnterpriseDTO(1L, "Sample");
		}

		@Override
		public DTO expectedDTO() {
			return new DefaultResponseEnterpriseDTO(1L,
					"Sample",
					LocalDate.of(1999, 1, 1));
		}
		
		public DTO expectedDTO(String name) {
			return new DefaultResponseEnterpriseDTO(1L,
					name,
					LocalDate.of(1999, 1, 1));
		}

		@Override
		public DTO updatedDTO() {
			return new UpdateEnterpriseDTO(1L, 
					"Updated name", 
					null);
		}

		@Override
		public DTO updatedDTOWithInvalidID() {
			return new UpdateEnterpriseDTO(-1L, 
					"Updated name", 
					null);
		}
		
		
		
	}
	
	public static class UserTest implements BodyObjectTest{
		
		
		public User createUser() {
			return new User(
					1L,
					"Sample",
					"sample@mail.com",
					"1234",
					new Role(RoleName.DEFAULT));
		}

		@Override
		public DTO createdDTO() {
			return new InsertUserDTO
					("Sample",
					"sample@mail.com",
					"12345",
					"DEFAULT");
		}

		@Override
		public DTO expectedCreatedDTO() {
			return new CreatedResponseUserDTO("Sample", "sample@mail.com");
		}
		
		public DTO createdDTOWithADMRole() {
			return new InsertUserDTO
					("Sample",
					"sample@mail.com",
					"12345",
					"ADM");
		}

		@Override
		public DTO expectedDTO() {
			return new DefaultResponseUserDTO("Sample", "sample@mail.com");
		}

		
		public DTO expectedDTO(String name) {
			return new DefaultResponseUserDTO(name, "sample@mail.com");
		}
		
		
		@Override
		public DTO updatedDTO() {
			return new UpdateUserDTO(1L,
					"Updated Sample",
					"sample@mail.com", 
					"1234");
		}

		@Override
		public DTO updatedDTOWithInvalidID() {
			return new UpdateUserDTO(-1L,
					"Updated Sample",
					"sample@mail.com", 
					"1234");
		}
		
		public DTO expectedUpdatedUserResponse() {
			return new UpdatedUserResponseDTO("Updated Sample", 
					"token", 
					LocalDateTime.now().plusHours(1L));
		}
		
	}
	
	public static class LoginTest {

		public LoginUserDTO createLoginTest() {
			return new LoginUserDTO("Sample",
					"sample@mail.com",
					"1234");
		}
		
		public LoginUserDTO createLoginTestWithoutEmail() {
			return new LoginUserDTO("Sample",
					null,
					"1234");
		}
		public LoginUserDTO createLoginTestWithoutNickname() {
			return new LoginUserDTO(null,
					"sample@mail.com",
					"1234");
		}
		
	}
}
