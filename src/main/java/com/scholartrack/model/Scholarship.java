package com.scholartrack.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "scholarships")
public class Scholarship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", nullable = false, length = 50)
    private ProviderType providerType;

    @Enumerated(EnumType.STRING)
    @Column(name = "scholarship_category", nullable = false, length = 50)
    private ScholarshipCategory scholarshipCategory;

    @Column(name = "financial_aid_amount", nullable = false)
    private Double financialAidAmount;

    @Column(name = "total_slots", nullable = false)
    private Integer totalSlots;

    @Column(name = "slots_remaining", nullable = false)
    private Integer slotsRemaining;

    @Column(name = "application_deadline", nullable = false)
    private LocalDate applicationDeadline;

    @Column(name = "academic_year", nullable = false, length = 30)
    private String academicYear;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @OneToOne(mappedBy = "scholarship", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private EligibilityRule eligibilityRule;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Scholarship() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public ProviderType getProviderType() { return providerType; }
    public void setProviderType(ProviderType providerType) { this.providerType = providerType; }
    public ScholarshipCategory getScholarshipCategory() { return scholarshipCategory; }
    public void setScholarshipCategory(ScholarshipCategory scholarshipCategory) { this.scholarshipCategory = scholarshipCategory; }
    public Double getFinancialAidAmount() { return financialAidAmount; }
    public void setFinancialAidAmount(Double financialAidAmount) { this.financialAidAmount = financialAidAmount; }
    public Integer getTotalSlots() { return totalSlots; }
    public void setTotalSlots(Integer totalSlots) { this.totalSlots = totalSlots; }
    public Integer getSlotsRemaining() { return slotsRemaining; }
    public void setSlotsRemaining(Integer slotsRemaining) { this.slotsRemaining = slotsRemaining; }
    public LocalDate getApplicationDeadline() { return applicationDeadline; }
    public void setApplicationDeadline(LocalDate applicationDeadline) { this.applicationDeadline = applicationDeadline; }
    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
    public EligibilityRule getEligibilityRule() { return eligibilityRule; }
    public void setEligibilityRule(EligibilityRule eligibilityRule) { this.eligibilityRule = eligibilityRule; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
