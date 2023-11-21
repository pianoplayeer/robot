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


/**
 * 用于处理用户退出登录的逻辑，将退出用户的聊天机器人（解释器DFAParser）删除
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
