package com.example.robot.security;

import com.example.robot.service.ChatBotService;
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
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

/**
 * @date 2023/11/14
 * @package com.example.robot.security
 */

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	private final UserDetailsService userDetailsService;
	private final LogoutSuccessHandler logoutSuccessHandler;
	
	@Autowired
	public SecurityConfig(UserDetailsService userDetailsService,
						  LogoutSuccessHandler logoutSuccessHandler) {
		this.userDetailsService = userDetailsService;
		this.logoutSuccessHandler = logoutSuccessHandler;
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
				.logout(
						c -> c.logoutSuccessUrl("/").logoutSuccessHandler(logoutSuccessHandler)
				)
				.userDetailsService(userDetailsService)
				.authorizeHttpRequests(
						c -> c.requestMatchers(HttpMethod.GET, "/", "/login", "/register", "/images/*").permitAll()
									 .requestMatchers(HttpMethod.POST, "/login", "/register").permitAll()
									 .anyRequest().authenticated()
				);
		
		return http.build();
	}
}
