package com.scholartrack.service;

import com.scholartrack.model.Notification;
import com.scholartrack.model.dto.PageResponse;
import com.scholartrack.repo.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public Notification sendNotification(Long userId, Long studentId, String appNumber, String title, String message, String type) {
        Notification notification = new Notification(userId, studentId, appNumber, title, message, type);
        return notificationRepository.save(notification);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Notification> getNotifications(Long studentId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Notification> pageResult;
        if (studentId != null) {
            pageResult = notificationRepository.findByStudentIdOrderByCreatedAtDesc(studentId, pageable);
        } else {
            pageResult = notificationRepository.findAllByOrderByCreatedAtDesc(pageable);
        }
        return new PageResponse<>(pageResult);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Notification> getRecentNotifications() {
        return notificationRepository.findTop20ByOrderByCreatedAtDesc();
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        notificationRepository.findById(id).ifPresent(n -> {
            n.setIsRead(true);
            notificationRepository.save(n);
        });
    }

    @Override
    @Transactional
    public void markAllAsRead(Long studentId) {
        List<Notification> list = notificationRepository.findAll();
        for (Notification n : list) {
            if (studentId == null || (n.getStudentId() != null && n.getStudentId().equals(studentId))) {
                n.setIsRead(true);
                notificationRepository.save(n);
            }
        }
    }
}
