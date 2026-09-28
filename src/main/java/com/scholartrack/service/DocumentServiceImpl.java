package com.scholartrack.service;

import com.scholartrack.model.*;
import com.scholartrack.model.dto.ApplicationResponse;
import com.scholartrack.model.dto.DocumentVerifyRequest;
import com.scholartrack.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final ApplicationDocumentRepository documentRepository;
    private final ScholarshipApplicationRepository applicationRepository;
    private final ApplicationTimelineRepository timelineRepository;
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public DocumentServiceImpl(ApplicationDocumentRepository documentRepository,
                               ScholarshipApplicationRepository applicationRepository,
                               ApplicationTimelineRepository timelineRepository,
                               AuditLogRepository auditLogRepository,
                               UserRepository userRepository,
                               NotificationService notificationService) {
        this.documentRepository = documentRepository;
        this.applicationRepository = applicationRepository;
        this.timelineRepository = timelineRepository;
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public ApplicationResponse.DocumentDto verifyDocument(DocumentVerifyRequest request) {
        ApplicationDocument doc = documentRepository.findById(request.getDocumentId())
                .orElseThrow(() -> new IllegalArgumentException("Document not found with ID: " + request.getDocumentId()));

        ScholarshipApplication app = doc.getApplication();
        if (app != null && (Boolean.TRUE.equals(app.getIsFlagged()) || Boolean.FALSE.equals(app.getEligibilityPassed()) || app.getStatus() == ApplicationStatus.ELIGIBILITY_FAILED)) {
            throw new IllegalStateException("Cannot verify document: Application " + app.getApplicationNumber() +
                    " is FLAGGED before manual review due to failed eligibility rules (" +
                    (app.getFlagReason() != null ? app.getFlagReason() : app.getEligibilityRemarks()) +
                    "). Ineligible applications cannot proceed through document verification.");
        }

        User verifier = null;
        if (request.getVerifierId() != null) {
            verifier = userRepository.findById(request.getVerifierId()).orElse(null);
        }

        DocumentStatus newStatus;
        try {
            newStatus = DocumentStatus.valueOf(request.getStatus().toUpperCase());
        } catch (Exception e) {
            newStatus = DocumentStatus.VERIFIED;
        }

        doc.setVerificationStatus(newStatus);
        doc.setRemarks(request.getRemarks() != null ? request.getRemarks() : "Document review completed.");
        doc.setVerifiedBy(verifier);
        doc.setVerifiedAt(LocalDateTime.now());
        ApplicationDocument savedDoc = documentRepository.save(doc);

        String verifierName = (verifier != null) ? verifier.getFullName() : "Document Verification Officer";

        // Audit Log
        auditLogRepository.save(new AuditLog(
                verifier != null ? verifier.getId() : null,
                verifierName,
                newStatus == DocumentStatus.VERIFIED ? AuditAction.DOCUMENT_VERIFIED : AuditAction.DOCUMENT_FLAGGED,
                "ApplicationDocument",
                doc.getId(),
                "Document " + doc.getDocumentType() + " marked as " + newStatus + ": " + doc.getRemarks()
        ));

        // Evaluate overall application document status
        List<ApplicationDocument> allDocs = documentRepository.findByApplicationId(app.getId());
        boolean hasFlagged = allDocs.stream().anyMatch(d -> d.getVerificationStatus() == DocumentStatus.RESUBMISSION_REQUESTED || d.getVerificationStatus() == DocumentStatus.REJECTED);
        boolean allVerified = allDocs.stream().allMatch(d -> d.getVerificationStatus() == DocumentStatus.VERIFIED);

        if (hasFlagged) {
            app.setStatus(ApplicationStatus.DOCUMENTS_FLAGGED);
            applicationRepository.save(app);

            timelineRepository.save(new ApplicationTimeline(
                    app,
                    "Document Verification Desk",
                    "REJECTED",
                    "Discrepancy flagged: " + doc.getDocumentType() + " - " + doc.getRemarks() + ". Action required by student.",
                    verifierName
            ));

            notificationService.sendNotification(
                    app.getStudent().getUser() != null ? app.getStudent().getUser().getId() : null,
                    app.getStudent().getId(),
                    app.getApplicationNumber(),
                    "⚠️ Action Needed: Document Flagged",
                    "Officer flagged " + doc.getDocumentType() + " for " + app.getApplicationNumber() + ": " + doc.getRemarks() + ". Please re-upload.",
                    "WARNING"
            );
        } else if (allVerified && !allDocs.isEmpty()) {
            app.setStatus(ApplicationStatus.UNDER_COMMITTEE_REVIEW);
            app.setVerifiedAt(LocalDateTime.now());
            app.setVerifiedBy(verifier);
            applicationRepository.save(app);

            timelineRepository.save(new ApplicationTimeline(
                    app,
                    "Document Verification Desk",
                    "COMPLETED",
                    "All required certificates and identity records successfully verified.",
                    verifierName
            ));

            timelineRepository.save(new ApplicationTimeline(
                    app,
                    "Committee Review & Sanction",
                    "IN_PROGRESS",
                    "Application forwarded to the selection panel for final award decision.",
                    "System"
            ));

            notificationService.sendNotification(
                    app.getStudent().getUser() != null ? app.getStudent().getUser().getId() : null,
                    app.getStudent().getId(),
                    app.getApplicationNumber(),
                    "✅ All Documents Cleared!",
                    "All mandatory verification documents for " + app.getApplicationNumber() + " were approved. Application moved to committee review.",
                    "SUCCESS"
            );
        } else {
            // Still in progress
            timelineRepository.save(new ApplicationTimeline(
                    app,
                    "Document Scrutiny Update",
                    "IN_PROGRESS",
                    doc.getDocumentType() + " verified as valid.",
                    verifierName
            ));

            notificationService.sendNotification(
                    app.getStudent().getUser() != null ? app.getStudent().getUser().getId() : null,
                    app.getStudent().getId(),
                    app.getApplicationNumber(),
                    "Document Verified",
                    doc.getDocumentType() + " for " + app.getApplicationNumber() + " verified as compliant.",
                    "SUCCESS"
            );
        }

        return mapToDto(savedDoc);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse.DocumentDto> getPendingDocuments() {
        return documentRepository.findByVerificationStatus(DocumentStatus.PENDING).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse.DocumentDto> getDocumentsByApplication(Long applicationId) {
        return documentRepository.findByApplicationId(applicationId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ApplicationResponse.DocumentDto resubmitDocument(Long documentId, String newDocumentName, String newDocumentPath) {
        ApplicationDocument doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found: " + documentId));

        doc.setDocumentName(newDocumentName);
        doc.setDocumentPath(newDocumentPath);
        doc.setVerificationStatus(DocumentStatus.PENDING);
        doc.setRemarks("Re-uploaded document by student. Pending re-scrutiny.");
        doc.setUploadedAt(LocalDateTime.now());
        ApplicationDocument saved = documentRepository.save(doc);

        ScholarshipApplication app = doc.getApplication();
        app.setStatus(ApplicationStatus.UNDER_DOCUMENT_VERIFICATION);
        applicationRepository.save(app);

        timelineRepository.save(new ApplicationTimeline(
                app,
                "Document Resubmission",
                "COMPLETED",
                "Updated document uploaded (" + newDocumentName + "). Returned to verification queue.",
                app.getStudent().getFullName() + " (Student)"
        ));

        return mapToDto(saved);
    }

    private ApplicationResponse.DocumentDto mapToDto(ApplicationDocument d) {
        ApplicationResponse.DocumentDto dto = new ApplicationResponse.DocumentDto();
        dto.setId(d.getId());
        dto.setDocumentType(d.getDocumentType().name());
        dto.setDocumentName(d.getDocumentName());
        dto.setDocumentPath(d.getDocumentPath());
        dto.setVerificationStatus(d.getVerificationStatus().name());
        dto.setRemarks(d.getRemarks());
        dto.setVerifiedByName(d.getVerifiedBy() != null ? d.getVerifiedBy().getFullName() : null);
        dto.setUploadedAt(d.getUploadedAt());
        dto.setVerifiedAt(d.getVerifiedAt());
        return dto;
    }
}
