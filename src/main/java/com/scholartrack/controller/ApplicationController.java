package com.scholartrack.controller;

import com.scholartrack.model.ApplicationStatus;
import com.scholartrack.model.dto.*;
import com.scholartrack.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@Tag(name = "Applications & Tracker", description = "Endpoints for scholarship application submission, timeline tracking, and approval lifecycle")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/apply")
    @Operation(summary = "Submit Scholarship Application",
               description = "Submits an application, triggers automated eligibility evaluation, assigns documents, and logs timeline")
    public ResponseEntity<ApplicationResponse> apply(@Valid @RequestBody ApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.apply(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Application by ID", description = "Returns full application status, documents, and journey timeline")
    public ResponseEntity<ApplicationResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.getApplicationById(id));
    }

    @GetMapping("/number/{applicationNumber}")
    @Operation(summary = "Track Application by Reference Number",
               description = "Allows students to track their application status in real-time with full visibility")
    public ResponseEntity<ApplicationResponse> getByNumber(@PathVariable String applicationNumber) {
        return ResponseEntity.ok(applicationService.getApplicationByNumber(applicationNumber));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "List Applications by Student", description = "Returns all scholarship applications for a student")
    public ResponseEntity<List<ApplicationResponse>> getByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(applicationService.getApplicationsByStudent(studentId));
    }

    @GetMapping
    @Operation(summary = "List All Applications", description = "Returns applications, optionally filtered by status")
    public ResponseEntity<List<ApplicationResponse>> getAll(
            @RequestParam(required = false) ApplicationStatus status) {
        return ResponseEntity.ok(applicationService.getAllApplications(status));
    }

    @GetMapping("/paged")
    @Operation(summary = "List Applications Paged & Sorted", description = "Returns paginated and sorted scholarship applications, optionally filtered by status")
    public ResponseEntity<PageResponse<ApplicationResponse>> getAllPaged(
            @RequestParam(required = false) ApplicationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "submittedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        return ResponseEntity.ok(applicationService.getAllApplicationsPaged(status, page, size, sortBy, direction));
    }

    @PostMapping("/decision")
    @Operation(summary = "Committee Decision (Approve / Reject)",
               description = "Final decision by committee, updates status, decrements quota, and appends timeline log")
    public ResponseEntity<ApplicationResponse> makeDecision(@Valid @RequestBody ApplicationDecisionRequest request) {
        return ResponseEntity.ok(applicationService.makeDecision(request));
    }

    @PostMapping("/disburse")
    @Operation(summary = "Direct Benefit Transfer (DBT) Disbursement",
               description = "Records scholarship grant disbursement with financial transaction reference (UTR)")
    public ResponseEntity<ApplicationResponse> disburse(@Valid @RequestBody DisbursementRequest request) {
        return ResponseEntity.ok(applicationService.disburse(request));
    }

    @PostMapping("/simulate-growth")
    @Operation(summary = "Simulate Rapid Application Growth",
               description = "Generates multiple applications across diverse workflows with live status change notifications for pagination testing")
    public ResponseEntity<List<ApplicationResponse>> simulateGrowth(@RequestParam(defaultValue = "12") int count) {
        return ResponseEntity.ok(applicationService.simulateDataGrowth(count));
    }
}
