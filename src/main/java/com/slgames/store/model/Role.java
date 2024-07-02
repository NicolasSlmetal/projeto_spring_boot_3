package com.slgames.store.model;

import org.springframework.security.core.GrantedAuthority;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name ="role")
@Table(name = "roles")
public class Role implements GrantedAuthority{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Getter
	public static enum RoleName{
		DEFAULT("Default"),
		STAFF("Staff"),
		ADM("ADM");
		
		private String roleName;
		
		RoleName(String name){
			roleName = name;
		}
		
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "name")
	@Enumerated(EnumType.STRING)
	private RoleName roleName; 
	
	public Role (RoleName name) {
		setRoleName(name);
		Long id;
		if (name.equals(RoleName.ADM)) id = 3L;
		else if (name.equals(RoleName.STAFF)) id = 2L;
		else id = 1L;
		setId(id);
	}

	@Override
	public String getAuthority() {
		// TODO Auto-generated method stub
		return getRoleName().toString();
	}
}
