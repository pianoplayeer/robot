package com.example.robot.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @date 2023/11/14
 * @package com.example.robot.web
 */

@Controller
@RequestMapping("/chatbot")
public class ChatBotController {

	@GetMapping
	public String showChatBotPage() {
		return "chatbot";
	}
}
