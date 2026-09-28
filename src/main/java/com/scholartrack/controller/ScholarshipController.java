package com.scholartrack.controller;

import com.scholartrack.model.ProviderType;
import com.scholartrack.model.dto.PageResponse;
import com.scholartrack.model.dto.ScholarshipDto;
import com.scholartrack.service.ScholarshipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scholarships")
@Tag(name = "Scholarships", description = "Endpoints for government, institutional, and CSR scholarship schemes")
public class ScholarshipController {

    private final ScholarshipService scholarshipService;

    public ScholarshipController(ScholarshipService scholarshipService) {
        this.scholarshipService = scholarshipService;
    }

    @GetMapping
    @Operation(summary = "List Active Scholarships", description = "Retrieves all currently active scholarships")
    public ResponseEntity<List<ScholarshipDto>> getAllActive(
            @RequestParam(required = false) ProviderType provider) {
        if (provider != null) {
            return ResponseEntity.ok(scholarshipService.getScholarshipsByProvider(provider));
        }
        return ResponseEntity.ok(scholarshipService.getAllActiveScholarships());
    }

    @GetMapping("/paged")
    @Operation(summary = "List Active Scholarships Paged & Sorted", description = "Retrieves paginated and sorted scholarships")
    public ResponseEntity<PageResponse<ScholarshipDto>> getAllActivePaged(
            @RequestParam(required = false) ProviderType provider,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "financialAidAmount") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        return ResponseEntity.ok(scholarshipService.getAllActiveScholarshipsPaged(provider, page, size, sortBy, direction));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Scholarship Details", description = "Retrieves a scholarship and its eligibility rules by ID")
    public ResponseEntity<ScholarshipDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(scholarshipService.getScholarshipById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get Scholarship by Code", description = "Retrieves a scholarship by code (e.g. GOV-CSSS-2026)")
    public ResponseEntity<ScholarshipDto> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(scholarshipService.getScholarshipByCode(code));
    }

    @PostMapping
    @Operation(summary = "Create Scholarship", description = "Creates a new scholarship along with its eligibility criteria")
    public ResponseEntity<ScholarshipDto> create(@Valid @RequestBody ScholarshipDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(scholarshipService.createScholarship(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Scholarship", description = "Updates an existing scholarship and eligibility rule")
    public ResponseEntity<ScholarshipDto> update(@PathVariable Long id, @Valid @RequestBody ScholarshipDto dto) {
        return ResponseEntity.ok(scholarshipService.updateScholarship(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete Scholarship", description = "Deactivates/deletes a scholarship")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        scholarshipService.deleteScholarship(id);
        return ResponseEntity.noContent().build();
    }
}
