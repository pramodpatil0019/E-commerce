package com.ecommerce.notificationservice.dto;

import lombok.Data;

@Data
public class NotificationRequest {
    private Long userId;
    private String email;
    private String subject;
    private String message;
    private String type;
}
