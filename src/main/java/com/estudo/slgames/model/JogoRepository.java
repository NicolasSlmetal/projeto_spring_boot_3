package com.estudo.slgames.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


public interface JogoRepository extends JpaRepository<Jogo, Long>{

	List<Jogo> findAllByDisponivelTrue();

}
