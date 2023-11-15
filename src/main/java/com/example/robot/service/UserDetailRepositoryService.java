package com.example.robot.service;

import com.example.robot.data.User;
import com.example.robot.data.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @date 2023/11/13
 * @package com.example.robot.service
 */

@Service
public class UserDetailRepositoryService
	implements UserDetailsService {
	
	final private UserRepository userRepos;
	
	@Autowired
	public UserDetailRepositoryService (UserRepository userRepos) {
		this.userRepos = userRepos;
	}
	
	@Override
	public UserDetails loadUserByUsername (String username)
			throws UsernameNotFoundException {
		User user = userRepos.findByUsername(username);
		
		if (user != null) {
			return user;
		}
		
		throw new UsernameNotFoundException(
				"User '" + username + "' not found"
		);
	}
}
