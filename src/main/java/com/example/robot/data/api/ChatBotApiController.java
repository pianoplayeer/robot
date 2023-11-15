package com.example.robot.data.api;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @date 2023/11/14
 * @package com.example.robot.data.api
 */

@RestController
@RequestMapping(path="/api/chatbot")
@CrossOrigin(origins="http://localhost:8080")
public class ChatBotApiController {
	
	private String botState = "start";
	
	@Value("${dfa.filename}")
	private String scriptFilename;
	
	private final List<Message> messages = new ArrayList<>();
	
	@PostMapping(path="/user", produces="application/json")
	public void sendUserMessage(@RequestBody Map<String, String> message) {
		// 处理用户消息
		messages.add(new Message("user", message.get("message")));
		
		// 模拟机器人的回复
		String reply = "I received your message: " + message.get("message");
		Message robotMessage = new Message("robot", reply);
		messages.add(robotMessage);
	}
	
	@GetMapping("/robot")
	public Message getBotMessage() {
		// 返回聊天记录
		return messages.get(messages.size() - 1);
	}
	
	@GetMapping("/history")
	public List<Message> getChatHistory() {
		// 返回聊天记录
		if (messages.isEmpty()) {
			messages.add(new Message("robot", "Hello, I am a robot."));
		}
		
		return messages;
	}
	
//	@PostMapping
////	@ResponseStatus(HttpStatus.CREATED)
////	public String answer(@RequestBody String content)
////		throws Exception {
////		try (
////				FileInputStream inputStream = new FileInputStream(scriptFilename)
////				) {
////			Yaml yaml = new Yaml();
////			Map<String, Object> stateMap = yaml.load(inputStream);
////
////
////		} catch (Exception exception) {
////			throw new Exception("Error reading script file: " + scriptFilename, exception);
////		}
////	}
	
	public String getCurrentUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			return authentication.getName();
		}
		return null;
	}
}

@Data
class Message {
	
	private String sender;
	private String content;
	
	public Message(String sender, String content) {
		this.sender = sender;
		this.content = content;
	}
	
}
