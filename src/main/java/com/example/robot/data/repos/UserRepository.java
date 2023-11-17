package com.example.robot.data.repos;

import com.example.robot.data.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
	
	User findByUsername(String username);
	
	@Modifying(clearAutomatically = true)
	@Transactional
	@Query("update User u set u.balance = u.balance + ?2 where u.username = ?1")
	void updateBalanceByUsername(String username, double balance);
}