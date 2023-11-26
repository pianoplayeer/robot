package com.example.robot.web;

import com.example.robot.data.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
	public String showChatBotPage (Model model, @AuthenticationPrincipal User user) {
		model.addAttribute("user", user.getUsername());
		return "chatbot" ;
	}
	
}
