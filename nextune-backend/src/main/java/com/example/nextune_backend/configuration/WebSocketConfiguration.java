package com.example.nextune_backend.configuration;

import com.example.nextune_backend.voice.handler.VoiceSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer, WebSocketConfigurer {

    private final VoiceSocketHandler handler;
    private final CookieJwtHandshakeInterceptor cookieJwt;

    public WebSocketConfiguration(VoiceSocketHandler handler,
                                  CookieJwtHandshakeInterceptor cookieJwt) {
        this.handler = handler;
        this.cookieJwt = cookieJwt;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/voice")
                .addInterceptors(cookieJwt)
                .setHandshakeHandler(new DefaultHandshakeHandler() {
                    @Override
                    protected Principal determineUser(ServerHttpRequest request,
                                                      WebSocketHandler wsHandler,
                                                      Map<String, Object> attributes) {
                        var p = attributes.get("principal");
                        return (p instanceof Principal) ? (Principal) p : null;
                    }
                })
                .setAllowedOrigins("*");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp")
                .setAllowedOriginPatterns("https://nextune.ddnsgeek.com","http://localhost:*", "http://127.0.0.1:*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

}
