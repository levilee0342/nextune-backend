package com.example.nextune_backend.service.impl;

import com.example.nextune_backend.dto.request.NotificationRequest;
import com.example.nextune_backend.entity.Notification;
import com.example.nextune_backend.repository.NotificationRepository;
import com.example.nextune_backend.service.EmailService;
import com.example.nextune_backend.service.NotificationService;
import com.example.nextune_backend.service.ProfileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ProfileService profileService; // để lấy email user từ id
    private final EmailService emailService;     // service gửi mail


    @Override
    @Transactional
    public List<Notification> createNotification(NotificationRequest request) {
        List<Notification> savedNotifications = new ArrayList<>();

        String senderEmail = profileService.getEmailByUserId(request.getSenderId());

        for (String receiverId : request.getReceiverIds()) {
            Notification notification = new Notification();
            notification.setSenderId(request.getSenderId());
            notification.setReceiverId(receiverId);
            notification.setType(request.getType());
            notification.setTitle(request.getTitle());
            notification.setContent(request.getContent());
            notification.setEntityId(request.getEntityId());
            notification.setIsRead(false);
            notification.setCreatedAt(LocalDateTime.now());


            Notification saved = notificationRepository.save(notification);
            savedNotifications.add(saved);

            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + receiverId,
                    saved
            );


            String receiverEmail = profileService.getEmailByUserId(receiverId);
            emailService.sendEmail(
                    receiverEmail,
                    "New Notification: " + saved.getTitle(),
                    "From: " + senderEmail + "\n\n" + saved.getContent()
            );
        }

        return savedNotifications;
    }


    @Override
    public List<Notification> getUserNotifications(String receiverId) {
        List<Notification> notifications =
                notificationRepository.findByReceiverIdOrderByCreatedAtDesc(receiverId);

        messagingTemplate.convertAndSend(
                "/topic/notifications/" + receiverId + "/all",
                notifications
        );

        return notifications;
    }


    @Override
    @Transactional
    public void markAsRead(String notificationId) {
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        n.setIsRead(true);
        notificationRepository.save(n);


        String receiverId = n.getReceiverId();
        List<Notification> updatedList =
                notificationRepository.findByReceiverIdOrderByCreatedAtDesc(receiverId);

        messagingTemplate.convertAndSend(
                "/topic/notifications/" + receiverId + "/all",
                updatedList
        );
    }

}
