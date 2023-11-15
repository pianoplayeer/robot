package com.example.robot.web;

import com.example.robot.data.User;
import lombok.Data;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;

/**
 * @date 2023/11/14
 * @package com.example.robot.web
 */

@Data
public class RegisterForm {
	
	private String username;
	private String password;
	private String confirm;
	
	public User toUser(PasswordEncoder encoder) {
		return new User(username, encoder.encode(password));
	}
}
