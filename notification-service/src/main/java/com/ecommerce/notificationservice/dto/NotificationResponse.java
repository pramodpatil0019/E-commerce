package com.ecommerce.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    private Long id;
    private Long userId;
    private String email;
    private String type;
    private String subject;
    private String message;
    private String status;
    private LocalDateTime createdAt;
}
