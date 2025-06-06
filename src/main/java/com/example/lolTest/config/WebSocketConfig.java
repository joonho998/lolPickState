package com.example.lolTest.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.example.lolTest.handler.DraftHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer
{
    @Override
    public void registerWebSocketHandlers(final WebSocketHandlerRegistry registry)
    {
        registry.addHandler(new DraftHandler(), "/ws/draft").setAllowedOrigins("*");
    }
}
