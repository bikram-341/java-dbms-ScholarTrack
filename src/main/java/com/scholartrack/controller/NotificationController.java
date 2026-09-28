package com.scholartrack.controller;

import com.scholartrack.model.Notification;
import com.scholartrack.model.dto.PageResponse;
import com.scholartrack.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notifications", description = "Real-time notifications for application status updates, eligibility checks, document review, and disbursements")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(summary = "Get Paginated Notifications", description = "Retrieves paginated notifications, optionally filtered by student ID")
    public ResponseEntity<PageResponse<Notification>> getNotifications(
            @RequestParam(required = false) Long studentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(notificationService.getNotifications(studentId, page, size));
    }

    @GetMapping("/recent")
    @Operation(summary = "Get Recent Notifications", description = "Retrieves top 20 recent notifications for quick dropdown polling")
    public ResponseEntity<List<Notification>> getRecentNotifications() {
        return ResponseEntity.ok(notificationService.getRecentNotifications());
    }

    @PostMapping("/{id}/read")
    @Operation(summary = "Mark Notification as Read", description = "Flags a specific notification as read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/read-all")
    @Operation(summary = "Mark All Notifications as Read", description = "Flags all notifications as read (optionally for a specific student)")
    public ResponseEntity<Void> markAllAsRead(@RequestParam(required = false) Long studentId) {
        notificationService.markAllAsRead(studentId);
        return ResponseEntity.ok().build();
    }
}
