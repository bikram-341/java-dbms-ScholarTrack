package com.scholartrack.repo;

import com.scholartrack.model.ApplicationDocument;
import com.scholartrack.model.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationDocumentRepository extends JpaRepository<ApplicationDocument, Long> {
    List<ApplicationDocument> findByApplicationId(Long applicationId);
    List<ApplicationDocument> findByVerificationStatus(DocumentStatus verificationStatus);
    long countByVerificationStatus(DocumentStatus verificationStatus);
}
