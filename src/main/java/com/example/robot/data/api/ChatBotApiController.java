package com.example.robot.data.api;

import com.example.robot.data.repos.UserRepository;
import com.example.robot.utils.DFAParser;
import com.example.robot.utils.Message;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @date 2023/11/14
 * @package com.example.robot.data.api
 */

@RestController
@RequestMapping(path="/api/chatbot")
@CrossOrigin(origins="http://localhost:8080")
@Data
public class ChatBotApiController {
	
	private UserRepository userRepos;
	
	private Map<String, DFAParser> userParserMap = new HashMap<>();
	
	private Environment env;
	
	private ResourceLoader loader;
	
	
	@Autowired
	public ChatBotApiController(UserRepository userRepos, ResourceLoader loader, Environment env) {
		this.userRepos = userRepos;
		this.loader = loader;
		this.env = env;
	}
	
	@PostMapping(path="/user", produces="application/json")
	public void sendUserMessage(@RequestBody Map<String, String> message) {
		DFAParser parser = userParserMap.get(getCurrentUsername());
		List<Message> messages = parser.getMsgList();
		parser.transferState(message.get("message"));
		
		// 处理用户消息
		messages.add(new Message("user", message.get("message")));
		
		// 模拟机器人的回复
		Message robotMessage = new Message("robot", parser.getCurrentResponse());
		messages.add(robotMessage);
	}
	
	@GetMapping("/robot")
	public Message getBotMessage() {
		DFAParser parser = userParserMap.get(getCurrentUsername());
		List<Message> messages = parser.getMsgList();
		
		// 返回聊天记录
		return messages.get(messages.size() - 1);
	}
	
	@GetMapping("/history")
	public List<Message> getChatHistory() {
		DFAParser parser;
		
		if (!userParserMap.containsKey(getCurrentUsername())) {
			parser = new DFAParser(userRepos, getCurrentUsername(),
									loader, env);
		} else {
			parser = userParserMap.get(getCurrentUsername());
		}
		List<Message> messages = parser.getMsgList();
		
		// 返回聊天记录
		if (messages.isEmpty()) {
			messages.add(new Message("robot", parser.getCurrentResponse()));
		}
		
		return messages;
	}
	
	public String getCurrentUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			return authentication.getName();
		}
		return null;
	}
}


