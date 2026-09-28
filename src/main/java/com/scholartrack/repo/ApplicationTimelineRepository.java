package com.scholartrack.repo;

import com.scholartrack.model.ApplicationTimeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationTimelineRepository extends JpaRepository<ApplicationTimeline, Long> {
    List<ApplicationTimeline> findByApplicationIdOrderByTimestampAsc(Long applicationId);
}
