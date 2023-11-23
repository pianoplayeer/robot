package com.example.robot.data.api;

import com.example.robot.service.ChatBotService;
import com.example.robot.utils.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ChatBotApiController.class)
@AutoConfigureMockMvc(addFilters = false)
class ChatBotApiControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@MockBean
	private ChatBotService mockService;
	
	
	@Test
	void testSendUserMessage() throws Exception {
		// Setup
		// Run the test
		final MockHttpServletResponse response = mockMvc.perform(post("/api/chatbot/user/{username}", "user")
																		 .param("message", "余额"))
				.andReturn().getResponse();
		
		// Verify the results
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
		verify(mockService).sendUserMessage("message", "user");
	}
	
	@Test
	void testGetBotMessage() throws Exception {
		// Setup
		when(mockService.getBotMessage("username")).thenReturn(new Message("sender", "content"));
		
		// Run the test
		final MockHttpServletResponse response = mockMvc.perform(get("/api/chatbot/robot/{username}", "username")
						.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		// Verify the results
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
	}
	
	@Test
	void testGetChatHistory() throws Exception {
		// Setup
		// Configure ChatBotService.getChatHistory(...).
		final List<Message> messages = List.of(new Message("sender", "content"));
		when(mockService.getChatHistory("username")).thenReturn(messages);
		
		// Run the test
		final MockHttpServletResponse response = mockMvc.perform(get("/api/chatbot/history/{username}", "username")
						.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		// Verify the results
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getContentAsString()).isEqualTo("expectedResponse");
	}
	
	@Test
	void testGetChatHistory_ChatBotServiceReturnsNoItems() throws Exception {
		// Setup
		when(mockService.getChatHistory("username")).thenReturn(Collections.emptyList());
		
		// Run the test
		final MockHttpServletResponse response = mockMvc.perform(get("/api/chatbot/history/{username}", "username")
						.accept(MediaType.APPLICATION_JSON))
				.andReturn().getResponse();
		
		// Verify the results
		assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
		assertThat(response.getContentAsString()).isEqualTo("[]");
	}
}
