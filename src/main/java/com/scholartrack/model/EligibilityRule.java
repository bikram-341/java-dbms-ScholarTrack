package com.scholartrack.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "eligibility_rules")
public class EligibilityRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scholarship_id", nullable = false, unique = true)
    @JsonBackReference
    private Scholarship scholarship;

    @Column(name = "min_gpa_or_percentage", nullable = false)
    private Double minGpaOrPercentage;

    @Column(name = "max_family_income", nullable = false)
    private Double maxFamilyIncome;

    @Column(name = "eligible_categories", length = 100)
    private String eligibleCategories = "ALL"; // e.g. "ALL" or "SC,ST,OBC"

    @Column(name = "eligible_degrees", length = 150)
    private String eligibleDegrees = "ALL"; // e.g. "ALL" or "UNDERGRADUATE,POSTGRADUATE"

    @Column(name = "eligible_gender", length = 30)
    private String eligibleGender = "ANY"; // ANY, FEMALE, MALE

    @Column(name = "min_age")
    private Integer minAge = 16;

    @Column(name = "max_age")
    private Integer maxAge = 35;

    @Column(name = "required_documents", nullable = false, length = 255)
    private String requiredDocuments; // comma-separated e.g. "INCOME_CERTIFICATE,GRADE_MARKSHEET"

    @Column(name = "rule_description", columnDefinition = "TEXT")
    private String ruleDescription;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public EligibilityRule() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Scholarship getScholarship() { return scholarship; }
    public void setScholarship(Scholarship scholarship) { this.scholarship = scholarship; }
    public Double getMinGpaOrPercentage() { return minGpaOrPercentage; }
    public void setMinGpaOrPercentage(Double minGpaOrPercentage) { this.minGpaOrPercentage = minGpaOrPercentage; }
    public Double getMaxFamilyIncome() { return maxFamilyIncome; }
    public void setMaxFamilyIncome(Double maxFamilyIncome) { this.maxFamilyIncome = maxFamilyIncome; }
    public String getEligibleCategories() { return eligibleCategories; }
    public void setEligibleCategories(String eligibleCategories) { this.eligibleCategories = eligibleCategories; }
    public String getEligibleDegrees() { return eligibleDegrees; }
    public void setEligibleDegrees(String eligibleDegrees) { this.eligibleDegrees = eligibleDegrees; }
    public String getEligibleGender() { return eligibleGender; }
    public void setEligibleGender(String eligibleGender) { this.eligibleGender = eligibleGender; }
    public Integer getMinAge() { return minAge; }
    public void setMinAge(Integer minAge) { this.minAge = minAge; }
    public Integer getMaxAge() { return maxAge; }
    public void setMaxAge(Integer maxAge) { this.maxAge = maxAge; }
    public String getRequiredDocuments() { return requiredDocuments; }
    public void setRequiredDocuments(String requiredDocuments) { this.requiredDocuments = requiredDocuments; }
    public String getRuleDescription() { return ruleDescription; }
    public void setRuleDescription(String ruleDescription) { this.ruleDescription = ruleDescription; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
