package com.scholartrack.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ApplicationDecisionRequest {
    @NotNull
    private Long applicationId;
    @NotBlank
    private String decision; // APPROVED or REJECTED or UNDER_COMMITTEE_REVIEW
    private String remarks;
    private Long reviewerId;

    public ApplicationDecisionRequest() {}

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }
    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }
}
