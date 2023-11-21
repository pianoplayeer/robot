package com.example.robot.utils;

import lombok.Data;

/**
 * 包装用户与机器人的消息，用于前端展示
 * 若sender为user，则为用户消息；若sender为robot，则为机器人消息
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
