package com.example.robot.service;

import com.example.robot.data.repos.UserRepository;
import com.example.robot.utils.DFAParser;
import com.example.robot.utils.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @date 2023/11/18
 * @package com.example.robot.service
 */

@Service
public class ChatBotService {
	
	private UserRepository userRepos;
	
	private Map<String, DFAParser> userParserMap = new HashMap<>();
	
	private Environment env;
	
	private ResourceLoader loader;
	
	
	public ChatBotService(UserRepository userRepos, ResourceLoader loader, Environment env) {
		this.userRepos = userRepos;
		this.loader = loader;
		this.env = env;
	}
	
	public void sendUserMessage(String message, String username) {
		DFAParser parser = userParserMap.get(username);
		List<Message> messages = parser.getMsgList();
		parser.transferState(message);
		
		// 处理用户消息
		messages.add(new Message("user", message));
		
		// 模拟机器人的回复
		Message robotMessage = new Message("robot", parser.getCurrentResponse());
		messages.add(robotMessage);
	}
	
	public Message getBotMessage(String username) {
		DFAParser parser = userParserMap.get(username);
		List<Message> messages = parser.getMsgList();
		
		// 返回聊天记录
		return messages.get(messages.size() - 1);
	}
	
	public List<Message> getChatHistory(String username) {
		DFAParser parser;
		
		if (!userParserMap.containsKey(username)) {
			parser = new DFAParser(userRepos, username,
					loader, env);
			userParserMap.put(username, parser);
		} else {
			parser = userParserMap.get(username);
		}
		List<Message> messages = parser.getMsgList();
		
		// 返回聊天记录
		if (messages.isEmpty()) {
			messages.add(new Message("robot", parser.getCurrentResponse()));
		}
		
		return messages;
	}
}
