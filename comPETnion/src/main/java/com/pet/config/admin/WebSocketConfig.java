package com.pet.config.admin;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig  implements WebSocketMessageBrokerConfigurer{
	@Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
		//假如有網站的前贅詞就需要加上去 我們這組的是comPETnion
        config.enableSimpleBroker("/comPETnion/topic");
        config.setApplicationDestinationPrefixes("/comPETnion/app");
    }

	@Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
        		//使用ngrok 網址(開發使用*就都可以)
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
