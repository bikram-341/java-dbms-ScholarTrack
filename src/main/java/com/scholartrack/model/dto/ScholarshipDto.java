package com.scholartrack.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public class ScholarshipDto {
    private Long id;
    @NotBlank(message = "Scholarship code is required")
    private String code;
    @NotBlank(message = "Scholarship title is required")
    private String title;
    private String description;
    @NotBlank(message = "Provider type is required (GOVERNMENT, INSTITUTIONAL, CORPORATE_CSR)")
    private String providerType;
    @NotBlank(message = "Scholarship category is required")
    private String scholarshipCategory;
    @NotNull(message = "Financial aid amount is required")
    @Positive(message = "Financial aid amount must be positive")
    private Double financialAidAmount;
    @NotNull(message = "Total slots is required")
    @Positive(message = "Total slots must be positive")
    private Integer totalSlots;
    private Integer slotsRemaining;
    private LocalDate applicationDeadline;
    private String academicYear;
    private Boolean isActive;

    // Eligibility criteria
    private Double minGpaOrPercentage;
    private Double maxFamilyIncome;
    private String eligibleCategories;
    private String eligibleDegrees;
    private String eligibleGender;
    private Integer minAge;
    private Integer maxAge;
    private String requiredDocuments;
    private String ruleDescription;

    public ScholarshipDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getProviderType() { return providerType; }
    public void setProviderType(String providerType) { this.providerType = providerType; }
    public String getScholarshipCategory() { return scholarshipCategory; }
    public void setScholarshipCategory(String scholarshipCategory) { this.scholarshipCategory = scholarshipCategory; }
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
}
