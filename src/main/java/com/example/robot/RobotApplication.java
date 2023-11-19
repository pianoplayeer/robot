package com.example.robot;

import com.example.robot.data.DataPackage;
import com.example.robot.data.User;
import com.example.robot.data.repos.DataPackageRepository;
import com.example.robot.data.repos.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.format.DateTimeFormatter;


@SpringBootApplication
public class RobotApplication {
	
	public static void main (String[] args) {
		SpringApplication.run(RobotApplication.class, args);
	}
	
	@Bean
	CommandLineRunner init(UserRepository userRepository, DataPackageRepository dataPackageRepository) {
		return args -> {
			User user1 = userRepository.findByUsername("jxy");
			//User user2 = userRepository.findByUsername("jxy");
			DataPackage dataPackage = dataPackageRepository.findByPackageName("套餐1");
			user1.getPackageList().add(dataPackage);
			//dataPackageRepository.save(dataPackage);
			userRepository.save(user1);
			//userRepository.save(user2);
		};
	}
}
