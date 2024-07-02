package com.slgames.store.model.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.slgames.store.dtos.users.LoginUserDTO;
import com.slgames.store.model.User;
import com.slgames.store.model.repository.UserRepository;

import lombok.Getter;

@Service
@Getter
public class LoginService implements UserDetailsService{


	private static final String EMAIL_PATTERN = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})$";
	
	@Autowired
	private UserRepository repository;
	
	public boolean nicknameIsPresent(LoginUserDTO dto) {
		return dto.nickname() != null;
	}
	
	public boolean emailIsPresent(LoginUserDTO dto) {
		return dto.email() != null;
	}
	
	public void validateNicknameOrEmailPresent(LoginUserDTO dto) {
		if (!emailIsPresent(dto) && !nicknameIsPresent(dto)) throw new IllegalArgumentException("Nickname or email not provided");
	}

	public String getDefaultNickname(LoginUserDTO dto) {
		
		return dto.nickname() != null? dto.nickname(): dto.email();
	}
	
	public User findByEmail(String email) {
		return getRepository().findByEmail(email);
	}
	
	public User findByNickname(String nickname) {
		return getRepository().findByNickname(nickname);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (username.matches(EMAIL_PATTERN)) return findByEmail(username);
		else return findByNickname(username);
	}

	
}
