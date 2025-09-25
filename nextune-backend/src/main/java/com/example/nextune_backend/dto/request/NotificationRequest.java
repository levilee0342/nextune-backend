package com.example.nextune_backend.dto.request;

import com.example.nextune_backend.entity.enums.NotificationType;
import lombok.Data;

import java.util.List;

@Data
public class NotificationRequest {
    private String senderId;
    private List<String> receiverIds; // gửi cho nhiều user
    private NotificationType type;
    private String title;
    private String content;
    private String entityId;
}
