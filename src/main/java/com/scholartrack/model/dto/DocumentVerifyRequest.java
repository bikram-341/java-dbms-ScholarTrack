package com.scholartrack.model.dto;

import jakarta.validation.constraints.NotNull;

public class DocumentVerifyRequest {
    @NotNull
    private Long documentId;
    @NotNull
    private String status; // VERIFIED, REJECTED, RESUBMISSION_REQUESTED
    private String remarks;
    private Long verifierId;

    public DocumentVerifyRequest() {}

    public Long getDocumentId() { return documentId; }
    public void setDocumentId(Long documentId) { this.documentId = documentId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public Long getVerifierId() { return verifierId; }
    public void setVerifierId(Long verifierId) { this.verifierId = verifierId; }
}
