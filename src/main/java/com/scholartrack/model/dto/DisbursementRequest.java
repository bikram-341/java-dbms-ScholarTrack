package com.scholartrack.model.dto;

import jakarta.validation.constraints.NotNull;

public class DisbursementRequest {
    @NotNull
    private Long applicationId;
    @NotNull
    private Double disbursedAmount;
    private String disbursementReference;
    private Long adminId;

    public DisbursementRequest() {}

    public Long getApplicationId() { return applicationId; }
    public void setApplicationId(Long applicationId) { this.applicationId = applicationId; }
    public Double getDisbursedAmount() { return disbursedAmount; }
    public void setDisbursedAmount(Double disbursedAmount) { this.disbursedAmount = disbursedAmount; }
    public String getDisbursementReference() { return disbursementReference; }
    public void setDisbursementReference(String disbursementReference) { this.disbursementReference = disbursementReference; }
    public Long getAdminId() { return adminId; }
    public void setAdminId(Long adminId) { this.adminId = adminId; }
}
