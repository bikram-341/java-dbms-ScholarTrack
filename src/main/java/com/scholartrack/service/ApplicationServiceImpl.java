package com.scholartrack.service;

import com.scholartrack.model.*;
import com.scholartrack.model.dto.*;
import com.scholartrack.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    private final ScholarshipApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final ScholarshipRepository scholarshipRepository;
    private final ApplicationDocumentRepository documentRepository;
    private final ApplicationTimelineRepository timelineRepository;
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final EligibilityService eligibilityService;
    private final NotificationService notificationService;

    public ApplicationServiceImpl(ScholarshipApplicationRepository applicationRepository,
                                  StudentRepository studentRepository,
                                  ScholarshipRepository scholarshipRepository,
                                  ApplicationDocumentRepository documentRepository,
                                  ApplicationTimelineRepository timelineRepository,
                                  AuditLogRepository auditLogRepository,
                                  UserRepository userRepository,
                                  EligibilityService eligibilityService,
                                  NotificationService notificationService) {
        this.applicationRepository = applicationRepository;
        this.studentRepository = studentRepository;
        this.scholarshipRepository = scholarshipRepository;
        this.documentRepository = documentRepository;
        this.timelineRepository = timelineRepository;
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
        this.eligibilityService = eligibilityService;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public ApplicationResponse apply(ApplicationRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + request.getStudentId()));

        Scholarship scholarship = scholarshipRepository.findById(request.getScholarshipId())
                .orElseThrow(() -> new IllegalArgumentException("Scholarship not found with ID: " + request.getScholarshipId()));

        if (!scholarship.getIsActive()) {
            throw new IllegalArgumentException("Scholarship '" + scholarship.getTitle() + "' is currently closed for applications.");
        }

        if (scholarship.getSlotsRemaining() <= 0) {
            throw new IllegalArgumentException("All available scholarship quota/slots are exhausted for this academic cycle.");
        }

        if (applicationRepository.existsByStudentIdAndScholarshipId(student.getId(), scholarship.getId())) {
            throw new IllegalArgumentException("You have already submitted an active application for scholarship: " + scholarship.getTitle());
        }

        // Run automated eligibility engine
        EligibilityCheckResponse check = eligibilityService.evaluateEligibility(scholarship.getId(), student);

        // Generate application reference number
        String appNum = "APP-" + LocalDateTime.now().getYear() + "-" + (10000 + new Random().nextInt(90000));

        ScholarshipApplication app = new ScholarshipApplication();
        app.setApplicationNumber(appNum);
        app.setStudent(student);
        app.setScholarship(scholarship);
        app.setStatus(check.isEligible() ? ApplicationStatus.UNDER_DOCUMENT_VERIFICATION : ApplicationStatus.ELIGIBILITY_FAILED);
        app.setEligibilityScore(check.getScore());
        app.setEligibilityPassed(check.isEligible());
        app.setEligibilityRemarks(check.isEligible() ?
                "Automated check passed. Score: " + check.getScore() + "%. Submitted for manual verification desk." :
                "Eligibility criteria deficit: " + String.join("; ", check.getFailedCriteria()));
        app.setSubmittedAt(LocalDateTime.now());

        ScholarshipApplication saved = applicationRepository.save(app);

        // Add application timeline event
        ApplicationTimeline t1 = new ApplicationTimeline(
                saved,
                "Application Submission",
                "COMPLETED",
                "Application submitted online. Initial rule assessment score: " + check.getScore() + "%.",
                student.getFullName() + " (Student)"
        );
        timelineRepository.save(t1);

        // Add document verification stage
        ApplicationTimeline t2 = new ApplicationTimeline(
                saved,
                "Document Verification Desk",
                check.isEligible() ? "IN_PROGRESS" : "REJECTED",
                check.isEligible() ? "Assigned to Verification Officer for document compliance review." : "Disqualified due to eligibility criteria.",
                "System Automation"
        );
        timelineRepository.save(t2);

        // Attach documents
        if (request.getDocuments() != null && !request.getDocuments().isEmpty()) {
            for (ApplicationRequest.DocumentUploadDto docDto : request.getDocuments()) {
                ApplicationDocument doc = new ApplicationDocument();
                doc.setApplication(saved);
                doc.setDocumentName(docDto.getDocumentName() != null ? docDto.getDocumentName() : "Uploaded_Doc.pdf");
                doc.setDocumentPath(docDto.getDocumentPath() != null ? docDto.getDocumentPath() : "/uploads/docs/" + doc.getDocumentName());
                try {
                    doc.setDocumentType(DocumentType.valueOf(docDto.getDocumentType()));
                } catch (Exception e) {
                    doc.setDocumentType(DocumentType.GRADE_MARKSHEET);
                }
                doc.setVerificationStatus(DocumentStatus.PENDING);
                doc.setRemarks("Uploaded during initial application. Awaiting verification officer check.");
                documentRepository.save(doc);
            }
        } else if (scholarship.getEligibilityRule() != null && scholarship.getEligibilityRule().getRequiredDocuments() != null) {
            // Auto-create document placeholders if required
            for (String reqDoc : scholarship.getEligibilityRule().getRequiredDocuments().split(",")) {
                String clean = reqDoc.trim();
                if (!clean.isEmpty()) {
                    ApplicationDocument doc = new ApplicationDocument();
                    doc.setApplication(saved);
                    doc.setDocumentName(clean.toLowerCase() + "_" + student.getStudentRollNo() + ".pdf");
                    doc.setDocumentPath("/uploads/docs/" + doc.getDocumentName());
                    try {
                        doc.setDocumentType(DocumentType.valueOf(clean));
                    } catch (Exception e) {
                        doc.setDocumentType(DocumentType.GRADE_MARKSHEET);
                    }
                    doc.setVerificationStatus(DocumentStatus.PENDING);
                    doc.setRemarks("Pending scrutiny by Document Officer.");
                    documentRepository.save(doc);
                }
            }
        }

        auditLogRepository.save(new AuditLog(
                student.getUser() != null ? student.getUser().getId() : null,
                student.getFullName(),
                AuditAction.APPLICATION_SUBMITTED,
                "ScholarshipApplication",
                saved.getId(),
                "New application " + appNum + " lodged for " + scholarship.getCode()
        ));

        notificationService.sendNotification(
                student.getUser() != null ? student.getUser().getId() : null,
                student.getId(),
                appNum,
                "Application Lodged Successfully",
                "Your application " + appNum + " for " + scholarship.getTitle() + " has been submitted. Rules Evaluation Score: " + check.getScore() + "%.",
                "INFO"
        );

        return getApplicationById(saved.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationById(Long id) {
        ScholarshipApplication app = applicationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Application not found with ID: " + id));
        return mapToResponse(app);
    }

    @Override
    @Transactional(readOnly = true)
    public ApplicationResponse getApplicationByNumber(String applicationNumber) {
        ScholarshipApplication app = applicationRepository.findByApplicationNumber(applicationNumber)
                .orElseThrow(() -> new IllegalArgumentException("Application not found with Number: " + applicationNumber));
        return mapToResponse(app);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getApplicationsByStudent(Long studentId) {
        return applicationRepository.findByStudentId(studentId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApplicationResponse> getAllApplications(ApplicationStatus statusFilter) {
        List<ScholarshipApplication> list = (statusFilter != null) ?
                applicationRepository.findByStatus(statusFilter) :
                applicationRepository.findAll();

        return list.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ApplicationResponse> getAllApplicationsPaged(ApplicationStatus statusFilter, int page, int size, String sortBy, String direction) {
        Sort.Direction dir = "ASC".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String field = (sortBy != null && !sortBy.isBlank()) ? sortBy : "submittedAt";
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, field));

        Page<ScholarshipApplication> paged = (statusFilter != null) ?
                applicationRepository.findByStatus(statusFilter, pageable) :
                applicationRepository.findAll(pageable);

        List<ApplicationResponse> content = paged.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new PageResponse<>(content, paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
    }

    @Override
    @Transactional
    public ApplicationResponse makeDecision(ApplicationDecisionRequest request) {
        ScholarshipApplication app = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new IllegalArgumentException("Application not found: " + request.getApplicationId()));

        User reviewer = null;
        if (request.getReviewerId() != null) {
            reviewer = userRepository.findById(request.getReviewerId()).orElse(null);
        }

        String reviewerName = (reviewer != null) ? reviewer.getFullName() : "Scholarship Selection Committee";
        String dec = request.getDecision().toUpperCase();

        if ("APPROVED".equals(dec)) {
            app.setStatus(ApplicationStatus.APPROVED);
            app.setDecisionAt(LocalDateTime.now());
            app.setDecisionRemarks(request.getRemarks() != null ? request.getRemarks() : "Application formally approved for scholarship grant.");

            // Decrement remaining slots
            Scholarship sc = app.getScholarship();
            if (sc.getSlotsRemaining() > 0) {
                sc.setSlotsRemaining(sc.getSlotsRemaining() - 1);
                scholarshipRepository.save(sc);
            }

            timelineRepository.save(new ApplicationTimeline(
                    app,
                    "Committee Review & Sanction",
                    "COMPLETED",
                    app.getDecisionRemarks(),
                    reviewerName
            ));

            timelineRepository.save(new ApplicationTimeline(
                    app,
                    "Direct Benefit Transfer (DBT)",
                    "PENDING",
                    "Awaiting finance disbursement processing.",
                    "Finance Division"
            ));

            auditLogRepository.save(new AuditLog(
                    reviewer != null ? reviewer.getId() : null,
                    reviewerName,
                    AuditAction.APPLICATION_APPROVED,
                    "ScholarshipApplication",
                    app.getId(),
                    "Application approved for grant amount: ₹" + app.getScholarship().getFinancialAidAmount()
            ));

            notificationService.sendNotification(
                    app.getStudent().getUser() != null ? app.getStudent().getUser().getId() : null,
                    app.getStudent().getId(),
                    app.getApplicationNumber(),
                    "🎉 Scholarship Award Sanctioned!",
                    "Congratulations! Application " + app.getApplicationNumber() + " for " + app.getScholarship().getTitle() + " has been approved for ₹" + app.getScholarship().getFinancialAidAmount().longValue() + ".",
                    "SUCCESS"
            );

        } else if ("REJECTED".equals(dec)) {
            app.setStatus(ApplicationStatus.REJECTED);
            app.setDecisionAt(LocalDateTime.now());
            app.setDecisionRemarks(request.getRemarks() != null ? request.getRemarks() : "Application rejected following committee evaluation.");

            timelineRepository.save(new ApplicationTimeline(
                    app,
                    "Committee Review & Sanction",
                    "REJECTED",
                    app.getDecisionRemarks(),
                    reviewerName
            ));

            auditLogRepository.save(new AuditLog(
                    reviewer != null ? reviewer.getId() : null,
                    reviewerName,
                    AuditAction.APPLICATION_REJECTED,
                    "ScholarshipApplication",
                    app.getId(),
                    "Application rejected: " + app.getDecisionRemarks()
            ));

            notificationService.sendNotification(
                    app.getStudent().getUser() != null ? app.getStudent().getUser().getId() : null,
                    app.getStudent().getId(),
                    app.getApplicationNumber(),
                    "Application Evaluation Notice",
                    "Application " + app.getApplicationNumber() + " was not approved by committee. Remark: " + app.getDecisionRemarks(),
                    "DANGER"
            );
        } else if ("UNDER_COMMITTEE_REVIEW".equals(dec)) {
            app.setStatus(ApplicationStatus.UNDER_COMMITTEE_REVIEW);
            timelineRepository.save(new ApplicationTimeline(
                    app,
                    "Committee Review & Sanction",
                    "IN_PROGRESS",
                    "Document scrutiny cleared. Scheduled for committee allocation review.",
                    reviewerName
            ));
        }

        return mapToResponse(applicationRepository.save(app));
    }

    @Override
    @Transactional
    public ApplicationResponse disburse(DisbursementRequest request) {
        ScholarshipApplication app = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new IllegalArgumentException("Application not found: " + request.getApplicationId()));

        if (app.getStatus() != ApplicationStatus.APPROVED) {
            throw new IllegalArgumentException("Only APPROVED applications can be disbursed. Current status: " + app.getStatus());
        }

        String utr = (request.getDisbursementReference() != null && !request.getDisbursementReference().isBlank()) ?
                request.getDisbursementReference() :
                "UTR-" + System.currentTimeMillis() % 100000000;

        app.setStatus(ApplicationStatus.DISBURSED);
        app.setDisbursedAmount(request.getDisbursedAmount() != null ? request.getDisbursedAmount() : app.getScholarship().getFinancialAidAmount());
        app.setDisbursedAt(LocalDateTime.now());
        app.setDisbursementReference(utr);

        ScholarshipApplication saved = applicationRepository.save(app);

        timelineRepository.save(new ApplicationTimeline(
                saved,
                "Direct Benefit Transfer (DBT)",
                "COMPLETED",
                "Grant of ₹" + saved.getDisbursedAmount() + " disbursed to beneficiary account (" +
                        saved.getStudent().getBankName() + "). Ref: " + utr,
                "Finance Department"
        ));

        auditLogRepository.save(new AuditLog(
                request.getAdminId(),
                "Finance Officer",
                AuditAction.DISBURSEMENT_INITIATED,
                "ScholarshipApplication",
                saved.getId(),
                "Disbursed ₹" + saved.getDisbursedAmount() + " via " + utr
        ));

        notificationService.sendNotification(
                saved.getStudent().getUser() != null ? saved.getStudent().getUser().getId() : null,
                saved.getStudent().getId(),
                saved.getApplicationNumber(),
                "💰 Direct Benefit Transfer (DBT) Released!",
                "Scholarship grant of ₹" + saved.getDisbursedAmount().longValue() + " has been successfully released to your bank account (" + saved.getStudent().getBankName() + "). Ref: " + utr,
                "SUCCESS"
        );

        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public List<ApplicationResponse> simulateDataGrowth(int count) {
        List<Student> students = studentRepository.findAll();
        List<Scholarship> scholarships = scholarshipRepository.findAll();
        if (students.isEmpty() || scholarships.isEmpty()) {
            return List.of();
        }

        User verifier = userRepository.findByUsername("officer_sharma").orElse(null);
        Random random = new Random();
        List<ApplicationResponse> createdList = new java.util.ArrayList<>();

        ApplicationStatus[] statuses = {
                ApplicationStatus.UNDER_DOCUMENT_VERIFICATION,
                ApplicationStatus.DOCUMENTS_FLAGGED,
                ApplicationStatus.UNDER_COMMITTEE_REVIEW,
                ApplicationStatus.APPROVED,
                ApplicationStatus.DISBURSED
        };

        for (int i = 0; i < count; i++) {
            Student student = students.get(random.nextInt(students.size()));
            Scholarship scholarship = scholarships.get(random.nextInt(scholarships.size()));
            ApplicationStatus status = statuses[random.nextInt(statuses.length)];

            String appNumber = "APP-2026-" + String.format("%05d", 1000 + applicationRepository.count() + 1 + i);

            ScholarshipApplication app = new ScholarshipApplication();
            app.setApplicationNumber(appNumber);
            app.setStudent(student);
            app.setScholarship(scholarship);
            app.setStatus(status);
            app.setEligibilityPassed(true);
            app.setEligibilityScore(70.0 + random.nextInt(28));
            app.setEligibilityRemarks("Automated eligibility check passed. Score: " + app.getEligibilityScore() + "%.");
            app.setSubmittedAt(LocalDateTime.now().minusHours(1 + random.nextInt(72)));

            if (status == ApplicationStatus.DOCUMENTS_FLAGGED) {
                app.setVerifiedAt(LocalDateTime.now().minusHours(1));
                app.setVerifiedBy(verifier);
                app.setDecisionRemarks("Income certificate illegible, resubmission requested.");
            } else if (status == ApplicationStatus.UNDER_COMMITTEE_REVIEW) {
                app.setVerifiedAt(LocalDateTime.now().minusHours(4));
                app.setVerifiedBy(verifier);
            } else if (status == ApplicationStatus.APPROVED) {
                app.setVerifiedAt(LocalDateTime.now().minusHours(6));
                app.setVerifiedBy(verifier);
                app.setDecisionAt(LocalDateTime.now().minusHours(2));
                app.setDecisionRemarks("Merit-cum-means criteria fulfilled. Sanctioned.");
            } else if (status == ApplicationStatus.DISBURSED) {
                app.setVerifiedAt(LocalDateTime.now().minusHours(10));
                app.setVerifiedBy(verifier);
                app.setDecisionAt(LocalDateTime.now().minusHours(5));
                app.setDecisionRemarks("Sanctioned by Committee.");
                app.setDisbursedAmount(scholarship.getFinancialAidAmount());
                app.setDisbursedAt(LocalDateTime.now().minusHours(1));
                app.setDisbursementReference("UTR-SBI-SIM-" + (10000000 + random.nextInt(90000000)));
            }

            ScholarshipApplication saved = applicationRepository.save(app);

            // Create initial doc
            ApplicationDocument doc1 = new ApplicationDocument();
            doc1.setApplication(saved);
            doc1.setDocumentType(DocumentType.GRADE_MARKSHEET);
            doc1.setDocumentName("Semester_Transcript_" + saved.getApplicationNumber() + ".pdf");
            doc1.setDocumentPath("/uploads/docs/marksheet.pdf");
            doc1.setVerificationStatus(status == ApplicationStatus.DOCUMENTS_FLAGGED ? DocumentStatus.RESUBMISSION_REQUESTED : (status == ApplicationStatus.UNDER_DOCUMENT_VERIFICATION ? DocumentStatus.PENDING : DocumentStatus.VERIFIED));
            doc1.setRemarks(status == ApplicationStatus.DOCUMENTS_FLAGGED ? "Missing official seal on grade sheet" : "Scrutiny completed");
            doc1.setVerifiedBy(verifier);
            doc1.setUploadedAt(saved.getSubmittedAt());
            documentRepository.save(doc1);

            // Add timeline
            timelineRepository.save(new ApplicationTimeline(saved, "Application Submitted", "COMPLETED", "Application registered under " + scholarship.getTitle(), student.getFullName()));

            // Send notification for status change
            String title = "Application " + saved.getApplicationNumber() + " Status: " + status.name();
            String msg = "Application for " + scholarship.getTitle() + " transitioned to stage " + status.name() + ".";
            String notifType = "INFO";
            if (status == ApplicationStatus.APPROVED || status == ApplicationStatus.DISBURSED) notifType = "SUCCESS";
            else if (status == ApplicationStatus.DOCUMENTS_FLAGGED) notifType = "WARNING";

            notificationService.sendNotification(
                    student.getUser() != null ? student.getUser().getId() : null,
                    student.getId(),
                    saved.getApplicationNumber(),
                    title,
                    msg,
                    notifType
            );

            createdList.add(mapToResponse(saved));
        }

        return createdList;
    }

    private ApplicationResponse mapToResponse(ScholarshipApplication app) {
        ApplicationResponse resp = new ApplicationResponse();
        resp.setId(app.getId());
        resp.setApplicationNumber(app.getApplicationNumber());

        Student st = app.getStudent();
        if (st != null) {
            resp.setStudentId(st.getId());
            resp.setStudentName(st.getFullName());
            resp.setStudentRollNo(st.getStudentRollNo());
            resp.setStudentEmail(st.getEmail());
            resp.setStudentPhone(st.getPhone());
            resp.setInstitutionName(st.getInstitutionName());
            resp.setDepartmentBranch(st.getDepartmentBranch());
            resp.setGpaOrPercentage(st.getGpaOrPercentage());
            resp.setFamilyAnnualIncome(st.getFamilyAnnualIncome());
            resp.setStudentCategory(st.getCategory() != null ? st.getCategory().name() : null);
            resp.setGender(st.getGender());
        }

        Scholarship sc = app.getScholarship();
        if (sc != null) {
            resp.setScholarshipId(sc.getId());
            resp.setScholarshipCode(sc.getCode());
            resp.setScholarshipTitle(sc.getTitle());
            resp.setProviderType(sc.getProviderType().name());
            resp.setFinancialAidAmount(sc.getFinancialAidAmount());
        }

        resp.setStatus(app.getStatus().name());
        resp.setEligibilityScore(app.getEligibilityScore());
        resp.setEligibilityPassed(app.getEligibilityPassed());
        resp.setEligibilityRemarks(app.getEligibilityRemarks());
        resp.setSubmittedAt(app.getSubmittedAt());
        resp.setVerifiedAt(app.getVerifiedAt());
        resp.setVerifiedByName(app.getVerifiedBy() != null ? app.getVerifiedBy().getFullName() : null);
        resp.setDecisionAt(app.getDecisionAt());
        resp.setDecisionRemarks(app.getDecisionRemarks());
        resp.setDisbursedAmount(app.getDisbursedAmount());
        resp.setDisbursedAt(app.getDisbursedAt());
        resp.setDisbursementReference(app.getDisbursementReference());

        // Map Documents
        List<ApplicationDocument> docs = documentRepository.findByApplicationId(app.getId());
        for (ApplicationDocument d : docs) {
            ApplicationResponse.DocumentDto dd = new ApplicationResponse.DocumentDto();
            dd.setId(d.getId());
            dd.setDocumentType(d.getDocumentType().name());
            dd.setDocumentName(d.getDocumentName());
            dd.setDocumentPath(d.getDocumentPath());
            dd.setVerificationStatus(d.getVerificationStatus().name());
            dd.setRemarks(d.getRemarks());
            dd.setVerifiedByName(d.getVerifiedBy() != null ? d.getVerifiedBy().getFullName() : null);
            dd.setUploadedAt(d.getUploadedAt());
            dd.setVerifiedAt(d.getVerifiedAt());
            resp.getDocuments().add(dd);
        }

        // Map Timeline
        List<ApplicationTimeline> timeline = timelineRepository.findByApplicationIdOrderByTimestampAsc(app.getId());
        for (ApplicationTimeline t : timeline) {
            ApplicationResponse.TimelineDto td = new ApplicationResponse.TimelineDto();
            td.setId(t.getId());
            td.setStageName(t.getStageName());
            td.setStatus(t.getStatus());
            td.setComments(t.getComments());
            td.setUpdatedByName(t.getUpdatedByName());
            td.setTimestamp(t.getTimestamp());
            resp.getTimeline().add(td);
        }

        return resp;
    }
}
