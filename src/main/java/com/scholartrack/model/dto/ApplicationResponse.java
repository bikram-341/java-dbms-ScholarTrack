package com.scholartrack.model.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ApplicationResponse {
    private Long id;
    private String applicationNumber;
    private Long studentId;
    private String studentName;
    private String studentRollNo;
    private String studentEmail;
    private String studentPhone;
    private String institutionName;
    private String departmentBranch;
    private Double gpaOrPercentage;
    private Double familyAnnualIncome;
    private String studentCategory;
    private String gender;

    private Long scholarshipId;
    private String scholarshipCode;
    private String scholarshipTitle;
    private String providerType;
    private Double financialAidAmount;

    private String status;
    private Double eligibilityScore;
    private Boolean eligibilityPassed;
    private String eligibilityRemarks;
    private LocalDateTime submittedAt;
    private LocalDateTime verifiedAt;
    private String verifiedByName;
    private LocalDateTime decisionAt;
    private String decisionRemarks;
    private Double disbursedAmount;
    private LocalDateTime disbursedAt;
    private String disbursementReference;

    private List<DocumentDto> documents = new ArrayList<>();
    private List<TimelineDto> timeline = new ArrayList<>();

    public static class DocumentDto {
        private Long id;
        private String documentType;
        private String documentName;
        private String documentPath;
        private String verificationStatus;
        private String remarks;
        private String verifiedByName;
        private LocalDateTime uploadedAt;
        private LocalDateTime verifiedAt;

        public DocumentDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getDocumentType() { return documentType; }
        public void setDocumentType(String documentType) { this.documentType = documentType; }
        public String getDocumentName() { return documentName; }
        public void setDocumentName(String documentName) { this.documentName = documentName; }
        public String getDocumentPath() { return documentPath; }
        public void setDocumentPath(String documentPath) { this.documentPath = documentPath; }
        public String getVerificationStatus() { return verificationStatus; }
        public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }
        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }
        public String getVerifiedByName() { return verifiedByName; }
        public void setVerifiedByName(String verifiedByName) { this.verifiedByName = verifiedByName; }
        public LocalDateTime getUploadedAt() { return uploadedAt; }
        public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
        public LocalDateTime getVerifiedAt() { return verifiedAt; }
        public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }
    }

    public static class TimelineDto {
        private Long id;
        private String stageName;
        private String status;
        private String comments;
        private String updatedByName;
        private LocalDateTime timestamp;

        public TimelineDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getStageName() { return stageName; }
        public void setStageName(String stageName) { this.stageName = stageName; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getComments() { return comments; }
        public void setComments(String comments) { this.comments = comments; }
        public String getUpdatedByName() { return updatedByName; }
        public void setUpdatedByName(String updatedByName) { this.updatedByName = updatedByName; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }

    public ApplicationResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getApplicationNumber() { return applicationNumber; }
    public void setApplicationNumber(String applicationNumber) { this.applicationNumber = applicationNumber; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getStudentRollNo() { return studentRollNo; }
    public void setStudentRollNo(String studentRollNo) { this.studentRollNo = studentRollNo; }
    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }
    public String getStudentPhone() { return studentPhone; }
    public void setStudentPhone(String studentPhone) { this.studentPhone = studentPhone; }
    public String getInstitutionName() { return institutionName; }
    public void setInstitutionName(String institutionName) { this.institutionName = institutionName; }
    public String getDepartmentBranch() { return departmentBranch; }
    public void setDepartmentBranch(String departmentBranch) { this.departmentBranch = departmentBranch; }
    public Double getGpaOrPercentage() { return gpaOrPercentage; }
    public void setGpaOrPercentage(Double gpaOrPercentage) { this.gpaOrPercentage = gpaOrPercentage; }
    public Double getFamilyAnnualIncome() { return familyAnnualIncome; }
    public void setFamilyAnnualIncome(Double familyAnnualIncome) { this.familyAnnualIncome = familyAnnualIncome; }
    public String getStudentCategory() { return studentCategory; }
    public void setStudentCategory(String studentCategory) { this.studentCategory = studentCategory; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
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
    public String getVerifiedByName() { return verifiedByName; }
    public void setVerifiedByName(String verifiedByName) { this.verifiedByName = verifiedByName; }
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
    public List<DocumentDto> getDocuments() { return documents; }
    public void setDocuments(List<DocumentDto> documents) { this.documents = documents; }
    public List<TimelineDto> getTimeline() { return timeline; }
    public void setTimeline(List<TimelineDto> timeline) { this.timeline = timeline; }
}
