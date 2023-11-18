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
	
	@PostMapping(path="/user/{username}", produces="application/json")
	public void sendUserMessage(@RequestBody Map<String, String> message,
								@PathVariable String username) {
		service.sendUserMessage(message.get("message"), username);
	}
	
	@GetMapping("/robot/{username}")
	public Message getBotMessage(@PathVariable String username) {
		return service.getBotMessage(username);
	}
	
	@GetMapping("/history/{username}")
	public List<Message> getChatHistory(@PathVariable String username) {
		return service.getChatHistory(username);
	}
	
}


