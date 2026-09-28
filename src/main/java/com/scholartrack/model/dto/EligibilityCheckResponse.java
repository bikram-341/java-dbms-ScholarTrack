package com.scholartrack.model.dto;

import java.util.ArrayList;
import java.util.List;

public class EligibilityCheckResponse {
    private Long scholarshipId;
    private String scholarshipCode;
    private String scholarshipTitle;
    private String providerType;
    private Double financialAidAmount;
    private boolean eligible;
    private Double score; // calculated match / merit score
    private List<String> passedCriteria = new ArrayList<>();
    private List<String> failedCriteria = new ArrayList<>();
    private List<String> requiredDocuments = new ArrayList<>();
    private String recommendationVerdict;

    public EligibilityCheckResponse() {}

    public Long getScholarshipId() { return scholarshipId; }
    public void setScholarshipId(Long scholarshipId) { this.scholarshipId = scholarshipId; }
    public String getScholarshipCode() { return scholarshipCode; }
    public void setScholarshipCode(String scholarshipCode) { this.scholarshipCode = scholarshipCode; }
    public String getScholarshipTitle() { return scholarshipTitle; }
    public void setScholarshipTitle(String scholarshipTitle) { this.scholarshipTitle = scholarshipTitle; }
    public String getProviderType() { return providerType; }
    public void setProviderType(String providerType) { this.providerType = providerType; }
    public Double getFinancialAidAmount() { return financialAidAmount; }
    public void setFinancialAidAmount(Double financialAidAmount) { this.financialAidAmount = financialAidAmount; }
    public boolean isEligible() { return eligible; }
    public void setEligible(boolean eligible) { this.eligible = eligible; }
    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
    public List<String> getPassedCriteria() { return passedCriteria; }
    public void setPassedCriteria(List<String> passedCriteria) { this.passedCriteria = passedCriteria; }
    public List<String> getFailedCriteria() { return failedCriteria; }
    public void setFailedCriteria(List<String> failedCriteria) { this.failedCriteria = failedCriteria; }
    public List<String> getRequiredDocuments() { return requiredDocuments; }
    public void setRequiredDocuments(List<String> requiredDocuments) { this.requiredDocuments = requiredDocuments; }
    public String getRecommendationVerdict() { return recommendationVerdict; }
    public void setRecommendationVerdict(String recommendationVerdict) { this.recommendationVerdict = recommendationVerdict; }
}
