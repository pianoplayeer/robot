package com.example.robot.utils;

import lombok.Data;

/**
 * @date 2023/11/17
 * @package com.example.robot.utils
 */

@Data
public class Message {
	
	private String sender;
	private String content;
	
	public Message(String sender, String content) {
		this.sender = sender;
		this.content = content;
	}
	
}
