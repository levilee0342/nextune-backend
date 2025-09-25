package com.example.nextune_backend.service;

import com.example.nextune_backend.dto.request.NotificationRequest;
import com.example.nextune_backend.entity.Notification;

import java.util.List;

public interface NotificationService {
    List<Notification> createNotification(NotificationRequest request);
    List<Notification> getUserNotifications(String receiverId);
    void markAsRead(String notificationId);
}