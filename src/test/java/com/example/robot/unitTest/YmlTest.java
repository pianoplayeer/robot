package com.example.robot.unitTest;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

/**
 * @date 2023/11/14
 * @package com.example.robot.unitTest
 */

public class YmlTest {
	public static void main(String[] args) {
		try(
				InputStream inputStream = new FileInputStream("src/main/resources/static/script/1.dfa")
		) {
			Yaml yaml = new Yaml();
			Map<String, Object> data = yaml.load(inputStream);
			System.out.println(data);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

