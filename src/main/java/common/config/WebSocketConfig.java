package common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import user.notify.websocket.NotifyWebSocketHandler;

@Configuration
@EnableWebSocket
@ComponentScan(basePackages = "user.notify")
public class WebSocketConfig implements WebSocketConfigurer {

	@Autowired
    private NotifyWebSocketHandler notifyWebSocketHandler;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(notifyWebSocketHandler, "/notify/notification")
				/*.setAllowedOrigins("*");*/
				.setAllowedOrigins("http://localhost:8080");
	}

	/*
	@Bean
	public WebSocketHandler webSocketHandler() {
		return notifyWebSocketHandler;
	}*/
}
