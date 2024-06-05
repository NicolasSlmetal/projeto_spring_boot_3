package com.slgames.store.model;

import com.slgames.store.dtos.users.InsertUserDTO;
import com.slgames.store.dtos.users.UpdateUserDTO;
import com.slgames.store.model.Role.RoleName;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "user")
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nickname;
	private String email;
	private String password;
	@ManyToOne
	@JoinColumn(name = "role")
	private Role role;
	
	
	public User (InsertUserDTO dto) {
		setNickname(dto.nickname());
		setEmail(dto.email());
		setPassword(dto.password());
		RoleName name = RoleName.valueOf(dto.role().toUpperCase());
		if (name.equals(RoleName.ADM)) throw new IllegalArgumentException("Cannot create user with ADM role");
		setRole(new Role(name));
	}


	public User(UpdateUserDTO dto) {
		setId(dto.id());
		String nickname = dto.nickname();
		String email = dto.email();
		String password = dto.password();
		if (email != null && 
				!email.isBlank() 
				&& !email.isEmpty()) setEmail(email);
		if (nickname != null && !nickname.isBlank() && !nickname.isEmpty()) setNickname(nickname);
		
		if ( password != null && !password.isBlank() && !password.isEmpty()) setPassword(password);
	}
	
}
