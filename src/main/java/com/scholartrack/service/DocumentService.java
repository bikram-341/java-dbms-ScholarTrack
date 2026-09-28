package com.scholartrack.service;

import com.scholartrack.model.dto.ApplicationResponse;
import com.scholartrack.model.dto.DocumentVerifyRequest;

import java.util.List;

public interface DocumentService {
    ApplicationResponse.DocumentDto verifyDocument(DocumentVerifyRequest request);
    List<ApplicationResponse.DocumentDto> getPendingDocuments();
    List<ApplicationResponse.DocumentDto> getDocumentsByApplication(Long applicationId);
    ApplicationResponse.DocumentDto resubmitDocument(Long documentId, String newDocumentName, String newDocumentPath);
}
