package com.example.robot.data.repos;

import com.example.robot.data.DataPackage;
import org.springframework.data.repository.CrudRepository;

public interface DataPackageRepository
		extends CrudRepository<DataPackage, Long> {
	
	DataPackage findByPackageName(String packageName);
}