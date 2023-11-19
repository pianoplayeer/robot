package com.example.robot.data;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @date 2023/11/18
 * @package com.example.robot.data
 */

@Entity
@Data
@RequiredArgsConstructor
@NoArgsConstructor(access= AccessLevel.PUBLIC, force=true)
public class DataPackage {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Column(unique = true)
	final private String packageName;
	
	final private int packagePrice;
	
}
