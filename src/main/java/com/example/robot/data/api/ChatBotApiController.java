package com.example.robot.data.api;

import com.example.robot.service.ChatBotService;
import com.example.robot.utils.Message;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @date 2023/11/14
 * @package com.example.robot.data.api
 */

/**
 * 用于提供聊天机器人的REST服务接口
 */
@RestController
@RequestMapping(path="/api/chatbot")
@CrossOrigin(origins="http://localhost:8080")
@Data
public class ChatBotApiController {
	
	private ChatBotService service;
	
	@Autowired
	public ChatBotApiController(ChatBotService service) {
		this.service = service;
	}
	
	/**
	 * 用于接收用户消息
	 *
	 * @param message 用户消息,前端传过来的json用Map接收，然后取出message字段
	 * @param username 用户名，用于区分不同用户的聊天记录
	 */
	@PostMapping(path="/user/{username}", produces="application/json")
	public void sendUserMessage(@RequestBody Map<String, String> message,
								@PathVariable String username) {
		service.sendUserMessage(message.get("message"), username);
	}
	
	/**
	 * 用于获取机器人消息,并返回给调用者
	 *
	 * @param username 用户名，用于区分不同用户的聊天记录
	 * @return 机器人消息
	 */
	@GetMapping("/robot/{username}")
	public Message getBotMessage(@PathVariable String username) {
		return service.getBotMessage(username);
	}
	
	/**
	 * @param username 用户名，用于区分不同用户的聊天记录
	 * @return 将用户和机器人的聊天记录返回给调用者
	 */
	@GetMapping("/history/{username}")
	public List<Message> getChatHistory(@PathVariable String username) {
		return service.getChatHistory(username);
	}
	
}


