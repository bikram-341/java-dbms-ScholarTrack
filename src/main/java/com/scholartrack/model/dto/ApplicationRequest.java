package com.scholartrack.model.dto;

import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class ApplicationRequest {
    @NotNull(message = "studentId is required")
    private Long studentId;
    @NotNull(message = "scholarshipId is required")
    private Long scholarshipId;
    private String notes;
    private List<DocumentUploadDto> documents = new ArrayList<>();

    public static class DocumentUploadDto {
        private String documentType;
        private String documentName;
        private String documentPath; // URL or base64 or storage path

        public DocumentUploadDto() {}

        public DocumentUploadDto(String documentType, String documentName, String documentPath) {
            this.documentType = documentType;
            this.documentName = documentName;
            this.documentPath = documentPath;
        }

        public String getDocumentType() { return documentType; }
        public void setDocumentType(String documentType) { this.documentType = documentType; }
        public String getDocumentName() { return documentName; }
        public void setDocumentName(String documentName) { this.documentName = documentName; }
        public String getDocumentPath() { return documentPath; }
        public void setDocumentPath(String documentPath) { this.documentPath = documentPath; }
    }

    public ApplicationRequest() {}

    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getScholarshipId() { return scholarshipId; }
    public void setScholarshipId(Long scholarshipId) { this.scholarshipId = scholarshipId; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<DocumentUploadDto> getDocuments() { return documents; }
    public void setDocuments(List<DocumentUploadDto> documents) { this.documents = documents; }
}
