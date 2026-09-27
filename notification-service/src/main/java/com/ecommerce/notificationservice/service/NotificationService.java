package com.ecommerce.notificationservice.service;

import com.ecommerce.notificationservice.dto.NotificationRequest;
import com.ecommerce.notificationservice.dto.NotificationResponse;
import com.ecommerce.notificationservice.model.Notification;
import com.ecommerce.notificationservice.model.NotificationStatus;
import com.ecommerce.notificationservice.model.NotificationType;
import com.ecommerce.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    @Transactional
    public NotificationResponse sendNotification(NotificationRequest request) {
        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .email(request.getEmail())
                .subject(request.getSubject())
                .message(request.getMessage())
                .type(NotificationType.valueOf(request.getType().toUpperCase()))
                .status(NotificationStatus.SENT)
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        emailService.sendEmail(savedNotification.getEmail(), savedNotification.getSubject(), savedNotification.getMessage());

        return mapToResponse(savedNotification);
    }

    public List<NotificationResponse> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .email(notification.getEmail())
                .type(notification.getType().name())
                .subject(notification.getSubject())
                .message(notification.getMessage())
                .status(notification.getStatus().name())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
