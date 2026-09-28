package com.scholartrack.service;

import com.scholartrack.model.ApplicationStatus;
import com.scholartrack.model.DocumentStatus;
import com.scholartrack.model.dto.DashboardSummaryDto;
import com.scholartrack.repo.ApplicationDocumentRepository;
import com.scholartrack.repo.ScholarshipApplicationRepository;
import com.scholartrack.repo.ScholarshipRepository;
import com.scholartrack.repo.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final ScholarshipRepository scholarshipRepository;
    private final StudentRepository studentRepository;
    private final ScholarshipApplicationRepository applicationRepository;
    private final ApplicationDocumentRepository documentRepository;

    public DashboardServiceImpl(ScholarshipRepository scholarshipRepository,
                                StudentRepository studentRepository,
                                ScholarshipApplicationRepository applicationRepository,
                                ApplicationDocumentRepository documentRepository) {
        this.scholarshipRepository = scholarshipRepository;
        this.studentRepository = studentRepository;
        this.applicationRepository = applicationRepository;
        this.documentRepository = documentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardSummaryDto getSummary() {
        DashboardSummaryDto dto = new DashboardSummaryDto();
        dto.setTotalScholarships(scholarshipRepository.count());
        dto.setTotalStudents(studentRepository.count());
        dto.setTotalApplications(applicationRepository.count());
        dto.setPendingVerifications(documentRepository.countByVerificationStatus(DocumentStatus.PENDING));
        dto.setApprovedApplications(applicationRepository.countByStatus(ApplicationStatus.APPROVED));
        dto.setRejectedApplications(applicationRepository.countByStatus(ApplicationStatus.REJECTED));
        dto.setDisbursedApplications(applicationRepository.countByStatus(ApplicationStatus.DISBURSED));
        Double disbursedSum = applicationRepository.sumTotalDisbursedAmount();
        dto.setTotalDisbursedAmount(disbursedSum != null ? disbursedSum : 0.0);
        return dto;
    }
}
