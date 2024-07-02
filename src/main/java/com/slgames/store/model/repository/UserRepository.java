package com.slgames.store.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.slgames.store.model.User;

public interface UserRepository extends JpaRepository<User, Long>{

	public boolean existsByNickname(String nickname);
	public boolean existsByEmail(String email);
	public boolean existsByPassword(String password);
	
	public User findByEmail(String email);
	public User findByNickname(String nickname);
	
}
