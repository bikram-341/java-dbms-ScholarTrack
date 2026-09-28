package com.scholartrack.repo;

import com.scholartrack.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Page<Notification> findByStudentIdOrderByCreatedAtDesc(Long studentId, Pageable pageable);
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Page<Notification> findAllByOrderByCreatedAtDesc(Pageable pageable);
    List<Notification> findTop20ByOrderByCreatedAtDesc();
    long countByIsReadFalse();
    long countByStudentIdAndIsReadFalse(Long studentId);
}
