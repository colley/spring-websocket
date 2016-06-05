package com.hs.spring.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.PerConnectionWebSocketHandler;

import com.hs.spring.websocket.chat.ChatHandShake;
import com.hs.spring.websocket.chat.ChatWebSocketHandler;
import com.hs.spring.websocket.echo.DefaultEchoService;
import com.hs.spring.websocket.echo.EchoWebSocketHandler;
import com.hs.spring.websocket.snake.SnakeWebSocketHandler;

@Configuration
@EnableWebMvc
@EnableWebSocket
public class WebConfig extends WebMvcConfigurerAdapter implements WebSocketConfigurer {


	
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(echoWebSocketHandler(), "/echo");
		registry.addHandler(snakeWebSocketHandler(), "/snake");

		registry.addHandler(echoWebSocketHandler(), "/sockjs/echo").withSockJS();
		registry.addHandler(snakeWebSocketHandler(), "/sockjs/snake").withSockJS();
		
		registry.addHandler(chatWebSocketHandler(), "/chat/{userId}").addInterceptors(new ChatHandShake());
        registry.addHandler(chatWebSocketHandler(), "/chat/sockjs/{userId}")
        .addInterceptors(new ChatHandShake()).withSockJS();
	}
	
	@Bean
	public WebSocketHandler chatWebSocketHandler() {
		return new ChatWebSocketHandler();
	}


	@Bean
	public WebSocketHandler echoWebSocketHandler() {
		return new EchoWebSocketHandler(echoService());
	}

	@Bean
	public WebSocketHandler snakeWebSocketHandler() {
		return new PerConnectionWebSocketHandler(SnakeWebSocketHandler.class);
	}

	@Bean
	public DefaultEchoService echoService() {
		return new DefaultEchoService("Did you say \"%s\"?");
	}

	// Allow serving HTML files through the default Servlet

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

}
