package com.example.nextune_backend.controller;

import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public void notifySessionExpired() {
        messagingTemplate.convertAndSend("/topic/session-expired",
                "SESSION_EXPIRED");
    }

}