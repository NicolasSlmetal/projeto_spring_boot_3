package com.slgames.store.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.slgames.store.dtos.game.InsertGameDTO;
import com.slgames.store.dtos.game.UpdateGameDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity(name = "game")
@Table(name = "games")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Builder
@ToString
public class Game implements Cloneable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "game_id")
	private Long id;
	@Column(name = "title")
	private String title;
	@Column(name = "launch_date")
	private LocalDate launchDate;
	@Column(name = "price")
	private Double price;
	@ManyToOne
	@JoinColumn(name = "developer")
	private Enterprise developer;
	@ManyToOne
	@JoinColumn(name = "publisher")
	private Enterprise publisher;
	@ManyToMany
	@JoinTable(name = "game_genres", joinColumns= {@JoinColumn(name = "game_id")}, 
	inverseJoinColumns = {@JoinColumn(name = "genre_id")})
	@JsonManagedReference
	private Set<Genre> genres;
	
	public Game(InsertGameDTO dto) {
		setTitle(dto.title());
		setLaunchDate(dto.launchDate());
		setPrice(dto.price());
		setDeveloper(new Enterprise(dto.developer()));
		setPublisher(new Enterprise(dto.publisher()));
		setGenres(new HashSet<Genre>(dto.genres().stream().map(genre -> new Genre(genre)).toList()));
	}
	
	@JsonIgnore
	public Long getDeveloperId() {
		return getDeveloper().getId();
	}
	
	@JsonIgnore
	public Long getPublisherId() {
		return getPublisher().getId();
	}
	@Override
	public Game clone() throws CloneNotSupportedException {
		return (Game) super.clone();
	}
	
	public void update(UpdateGameDTO gameDto) {
		if (gameDto.title() != null && !gameDto.title().isEmpty()) setTitle(gameDto.title());
		if (gameDto.launchDate() != null) setLaunchDate(gameDto.launchDate());
		if (gameDto.price() != null) setPrice(gameDto.price());
	}
	
	
}
