package com.scholartrack.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "scholarship_applications")
public class ScholarshipApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_number", nullable = false, unique = true, length = 50)
    private String applicationNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "scholarship_id", nullable = false)
    private Scholarship scholarship;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ApplicationStatus status = ApplicationStatus.SUBMITTED;

    @Column(name = "eligibility_score")
    private Double eligibilityScore = 0.0;

    @Column(name = "eligibility_passed")
    private Boolean eligibilityPassed = false;

    @Column(name = "eligibility_remarks", length = 500)
    private String eligibilityRemarks;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by_id")
    private User verifiedBy;

    @Column(name = "decision_at")
    private LocalDateTime decisionAt;

    @Column(name = "decision_remarks", length = 500)
    private String decisionRemarks;

    @Column(name = "disbursed_amount")
    private Double disbursedAmount = 0.0;

    @Column(name = "disbursed_at")
    private LocalDateTime disbursedAt;

    @Column(name = "disbursement_reference", length = 100)
    private String disbursementReference;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ApplicationDocument> documents = new ArrayList<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ApplicationTimeline> timeline = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public ScholarshipApplication() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.submittedAt == null) {
            this.submittedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getApplicationNumber() { return applicationNumber; }
    public void setApplicationNumber(String applicationNumber) { this.applicationNumber = applicationNumber; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public Scholarship getScholarship() { return scholarship; }
    public void setScholarship(Scholarship scholarship) { this.scholarship = scholarship; }
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    public Double getEligibilityScore() { return eligibilityScore; }
    public void setEligibilityScore(Double eligibilityScore) { this.eligibilityScore = eligibilityScore; }
    public Boolean getEligibilityPassed() { return eligibilityPassed; }
    public void setEligibilityPassed(Boolean eligibilityPassed) { this.eligibilityPassed = eligibilityPassed; }
    public String getEligibilityRemarks() { return eligibilityRemarks; }
    public void setEligibilityRemarks(String eligibilityRemarks) { this.eligibilityRemarks = eligibilityRemarks; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
    public User getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(User verifiedBy) { this.verifiedBy = verifiedBy; }
    public LocalDateTime getDecisionAt() { return decisionAt; }
    public void setDecisionAt(LocalDateTime decisionAt) { this.decisionAt = decisionAt; }
    public String getDecisionRemarks() { return decisionRemarks; }
    public void setDecisionRemarks(String decisionRemarks) { this.decisionRemarks = decisionRemarks; }
    public Double getDisbursedAmount() { return disbursedAmount; }
    public void setDisbursedAmount(Double disbursedAmount) { this.disbursedAmount = disbursedAmount; }
    public LocalDateTime getDisbursedAt() { return disbursedAt; }
    public void setDisbursedAt(LocalDateTime disbursedAt) { this.disbursedAt = disbursedAt; }
    public String getDisbursementReference() { return disbursementReference; }
    public void setDisbursementReference(String disbursementReference) { this.disbursementReference = disbursementReference; }
    public List<ApplicationDocument> getDocuments() { return documents; }
    public void setDocuments(List<ApplicationDocument> documents) { this.documents = documents; }
    public List<ApplicationTimeline> getTimeline() { return timeline; }
    public void setTimeline(List<ApplicationTimeline> timeline) { this.timeline = timeline; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
