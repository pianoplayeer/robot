package com.example.robot.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @date 2023/11/13
 * @package com.example.robot.data
 */

@Entity
@Data
@NoArgsConstructor(access= AccessLevel.PRIVATE, force=true)
@RequiredArgsConstructor
public class User implements UserDetails {
	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	private Long id;
	
	private final String username;
	private final String password;
	private double balance;
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities () {
		return List.of(new SimpleGrantedAuthority("ROLE_USER"));
	}
	
	@Override
	public boolean isAccountNonExpired () {
		return true;
	}
	
	@Override
	public boolean isAccountNonLocked () {
		return true;
	}
	
	@Override
	public boolean isCredentialsNonExpired () {
		return true;
	}
	
	@Override
	public boolean isEnabled () {
		return true;
	}
}
