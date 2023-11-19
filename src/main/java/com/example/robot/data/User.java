package com.example.robot.data;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @date 2023/11/13
 * @package com.example.robot.data
 */

@Entity
@Data
@NoArgsConstructor(access= AccessLevel.PUBLIC, force=true)
@RequiredArgsConstructor
public class User implements UserDetails {
	@Id
	@GeneratedValue(strategy= GenerationType.AUTO)
	private Long id;
	
	private final String username;
	private final String password;
	private double balance;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "user_package_list", // 中间表的名称
			joinColumns = @JoinColumn(name = "user_id"), // 当前实体在中间表中的外键列名
			inverseJoinColumns = @JoinColumn(name = "package_id") // 关联实体在中间表中的外键列名
	)
	private final List<DataPackage> packageList = new ArrayList<>();
	
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
