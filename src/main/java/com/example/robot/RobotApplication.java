package com.example.robot;

import com.example.robot.data.User;
import com.example.robot.data.repos.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class RobotApplication {
	
	public static void main (String[] args) {
		SpringApplication.run(RobotApplication.class, args);
	}
	
}
