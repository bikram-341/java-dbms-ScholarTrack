package com.scholartrack.repo;

import com.scholartrack.model.ApplicationStatus;
import com.scholartrack.model.ScholarshipApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScholarshipApplicationRepository extends JpaRepository<ScholarshipApplication, Long> {
    Optional<ScholarshipApplication> findByApplicationNumber(String applicationNumber);
    List<ScholarshipApplication> findByStudentId(Long studentId);
    Page<ScholarshipApplication> findByStudentId(Long studentId, Pageable pageable);
    List<ScholarshipApplication> findByScholarshipId(Long scholarshipId);
    List<ScholarshipApplication> findByStatus(ApplicationStatus status);
    Page<ScholarshipApplication> findByStatus(ApplicationStatus status, Pageable pageable);
    boolean existsByStudentIdAndScholarshipId(Long studentId, Long scholarshipId);
    long countByStatus(ApplicationStatus status);

    @Query("SELECT COALESCE(SUM(sa.disbursedAmount), 0.0) FROM ScholarshipApplication sa WHERE sa.status = com.scholartrack.model.ApplicationStatus.DISBURSED")
    Double sumTotalDisbursedAmount();
}
