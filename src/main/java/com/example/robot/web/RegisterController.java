package com.example.robot.web;

import com.example.robot.data.User;
import com.example.robot.data.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @date 2023/11/14
 * @package com.example.robot.web
 */

@Controller
@RequestMapping("/register")
public class RegisterController {
	
	private UserRepository userRepos;
	private PasswordEncoder passwordEncoder;
	
	public RegisterController(UserRepository userRepos, PasswordEncoder passwordEncoder) {
		this.userRepos = userRepos;
		this.passwordEncoder = passwordEncoder;
	}
	
	@GetMapping
	public String showRegisterPage() {
		return "register";
	}
	
	@PostMapping
	public String processRegistration(RegisterForm form) {
		if (form.getUsername().equals("") || form.getPassword().equals("")) {
			return "redirect:/register?blank";
		}
		
		if (form.getPassword().equals(form.getConfirm())) {
			User user = form.toUser(passwordEncoder);
			
			if (userRepos.findByUsername(user.getUsername()) != null) {
				return "redirect:/register?repeat";
			}
			
			userRepos.save(user);
			return "redirect:/login";
		}
		
		return "redirect:/register?error";
	}
}
