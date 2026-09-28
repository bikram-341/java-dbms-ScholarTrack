package com.scholartrack.controller;

import com.scholartrack.model.dto.ApplicationResponse;
import com.scholartrack.model.dto.DocumentVerifyRequest;
import com.scholartrack.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@Tag(name = "Document Verification Desk", description = "Endpoints for manual document verification, remark feedback, and resubmission tracking")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify or Flag Document",
               description = "Allows verification officer to mark a document VERIFIED or RESUBMISSION_REQUESTED with explicit remarks")
    public ResponseEntity<ApplicationResponse.DocumentDto> verifyDocument(@Valid @RequestBody DocumentVerifyRequest request) {
        return ResponseEntity.ok(documentService.verifyDocument(request));
    }

    @GetMapping("/pending")
    @Operation(summary = "Pending Documents Queue",
               description = "Retrieves all unverified documents awaiting officer scrutiny to resolve delays")
    public ResponseEntity<List<ApplicationResponse.DocumentDto>> getPendingDocuments() {
        return ResponseEntity.ok(documentService.getPendingDocuments());
    }

    @GetMapping("/application/{applicationId}")
    @Operation(summary = "Get Application Documents",
               description = "Retrieves all uploaded documents with verification status for a specific application")
    public ResponseEntity<List<ApplicationResponse.DocumentDto>> getDocumentsByApplication(@PathVariable Long applicationId) {
        return ResponseEntity.ok(documentService.getDocumentsByApplication(applicationId));
    }

    @PostMapping("/{documentId}/resubmit")
    @Operation(summary = "Resubmit Flagged Document",
               description = "Allows student to upload corrected document after reviewer feedback")
    public ResponseEntity<ApplicationResponse.DocumentDto> resubmit(
            @PathVariable Long documentId,
            @RequestBody Map<String, String> body) {
        String docName = body.getOrDefault("documentName", "Updated_Document.pdf");
        String docPath = body.getOrDefault("documentPath", "/uploads/docs/" + docName);
        return ResponseEntity.ok(documentService.resubmitDocument(documentId, docName, docPath));
    }
}
