package com.example.robot.security;

import com.example.robot.service.ChatBotService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @date 2023/11/21
 * @package com.example.robot.security
 */

@Component
@Slf4j
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
	
	private final ChatBotService chatBotService;
	
	public LogoutSuccessHandlerImpl(ChatBotService chatBotService) {
		this.chatBotService = chatBotService;
	}
	
	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		String user = authentication.getName();
		
		log.info("用户{}退出登录。", user);
		chatBotService.deleteUserChatBot(user);
		response.sendRedirect("/");
	}
}
