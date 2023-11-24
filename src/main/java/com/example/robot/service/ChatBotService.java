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

/**
 * 聊天机器人服务类，将机器人操作封装成服务，供对应的RestController调用
 *
 * @apiNote 该类方法的注释均在ChatBotApiController中写过，这里就不重复写了，
 * 			对应的方法名称相同。
 */
@Service
public class ChatBotService {
	
	private final RepositoryService reposService;
	
	private final Map<String, DFAParser> userParserMap = new HashMap<>();
	
	private final Environment env;
	
	private final ResourceLoader loader;
	
	
	public ChatBotService(RepositoryService reposService, ResourceLoader loader,
						  Environment env) {
		this.reposService = reposService;
		this.loader = loader;
		this.env = env;
	}
	
	/**
	 * 删除用户聊天机器人
	 * @param user 用户名
	 */
	public void deleteUserChatBot(String user) {
		userParserMap.remove(user);
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
		
		if (parser != null) {
			List<Message> messages = parser.getMsgList();
			
			// 返回聊天记录
			return messages.get(messages.size() - 1);
		} else {
			return getChatHistory(username).get(0);
		}
		
	}
	
	public List<Message> getChatHistory(String username) {
		DFAParser parser;
		
		if (!userParserMap.containsKey(username)) {
			parser = new DFAParser(reposService, username,
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
