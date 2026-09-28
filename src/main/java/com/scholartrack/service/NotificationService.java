package com.scholartrack.service;

import com.scholartrack.model.Notification;
import com.scholartrack.model.dto.PageResponse;

import java.util.List;

public interface NotificationService {
    Notification sendNotification(Long userId, Long studentId, String appNumber, String title, String message, String type);
    PageResponse<Notification> getNotifications(Long studentId, int page, int size);
    List<Notification> getRecentNotifications();
    void markAsRead(Long id);
    void markAllAsRead(Long studentId);
}
