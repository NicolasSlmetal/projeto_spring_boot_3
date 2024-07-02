package com.slgames.store.model;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
public class User implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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


	public void updateUser(UpdateUserDTO dto) {
		setId(dto.id());
		String nickname = dto.nickname();
		String email = dto.email();
		String password = dto.password();
		if (email != null && 
				!email.isBlank() 
				&& !email.isEmpty()) setEmail(email);
		if (nickname != null && !nickname.isBlank() && !nickname.isEmpty()) setNickname(nickname);
		
		if (password != null && !password.isBlank() && !password.isEmpty()) setPassword(password);
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		RoleName role = getRole().getRoleName();
		switch (role) {
			case ADM:
				return List.of(getRole(), new Role(RoleName.STAFF), new Role(RoleName.DEFAULT));
			case STAFF:
				return List.of(getRole(), new Role(RoleName.DEFAULT));
			default: 
				return List.of(getRole());
		}
		
	}


	@Override
	public String getUsername() {
		return  getNickname() != null?getNickname(): getEmail();
	}
	@Override
	public String getPassword() {
		return password;
	}


	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}


	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return true;
	}


	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return true;
	}


	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}
	
}
