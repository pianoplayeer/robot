package com.example.robot;

import com.example.robot.data.DataPackage;
import com.example.robot.data.User;
import com.example.robot.data.repos.DataPackageRepository;
import com.example.robot.data.repos.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.format.DateTimeFormatter;
import java.util.List;


@SpringBootApplication
@Slf4j
public class RobotApplication {
	
	public static void main (String[] args) {
		SpringApplication.run(RobotApplication.class, args);
	}
	
	@Bean
	CommandLineRunner init(UserRepository userRepository, DataPackageRepository dataPackageRepository) {
		return args -> {
			log.info(userRepository.findByUsername("jxy").toString());
		};
	}
}
