package com.example.robot.service;

import com.example.robot.data.repos.DataPackageRepository;
import com.example.robot.data.repos.UserRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

/**
 * @date 2023/11/19
 * @package com.example.robot.service
 */

/**
 * 将所有的Repository注入到该类中，供其他Service调用，便于后续扩展
 */
@Service
@Data
public class RepositoryService {
	
	private UserRepository userRepos;
	private DataPackageRepository dataPackageRepos;
	
	public RepositoryService(UserRepository userRepos, DataPackageRepository dataPackageRepos) {
		this.userRepos = userRepos;
		this.dataPackageRepos = dataPackageRepos;
	}
}
