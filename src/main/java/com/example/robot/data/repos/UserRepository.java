package com.example.robot.data.repos;

import com.example.robot.data.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
	
	User findByUsername(String username);
	double findBalanceByUsername(String username);
}