package com.example.robot.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @date 2023/11/14
 * @package com.example.robot.security
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	private final UserDetailsService userDetailsService;
	
	@Autowired
	public SecurityConfig(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}
	
	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.formLogin(
						c -> c.loginPage("/login").defaultSuccessUrl("/chatbot", true)
									 .failureUrl("/login?error")
				)
				.userDetailsService(userDetailsService)
				.authorizeHttpRequests(
						c -> c.requestMatchers(HttpMethod.GET, "/", "/login", "/register", "/images/*").permitAll()
								.anyRequest().authenticated()
				);
		
		return http.build();
	}
}
