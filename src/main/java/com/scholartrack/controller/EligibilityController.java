package com.scholartrack.controller;

import com.scholartrack.model.Student;
import com.scholartrack.model.dto.EligibilityCheckRequest;
import com.scholartrack.model.dto.EligibilityCheckResponse;
import com.scholartrack.repo.StudentRepository;
import com.scholartrack.service.EligibilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eligibility")
@Tag(name = "Eligibility Rules Engine", description = "Endpoints for evaluating student eligibility against scholarship rules")
public class EligibilityController {

    private final EligibilityService eligibilityService;
    private final StudentRepository studentRepository;

    public EligibilityController(EligibilityService eligibilityService,
                                 StudentRepository studentRepository) {
        this.eligibilityService = eligibilityService;
        this.studentRepository = studentRepository;
    }

    @PostMapping("/evaluate")
    @Operation(summary = "Evaluate Student Eligibility for a Scholarship",
               description = "Checks a student's GPA, income, category, degree, and gender against scholarship rules")
    public ResponseEntity<EligibilityCheckResponse> evaluate(
            @RequestParam Long scholarshipId,
            @RequestParam Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + studentId));
        return ResponseEntity.ok(eligibilityService.evaluateEligibility(scholarshipId, student));
    }

    @PostMapping("/check-custom")
    @Operation(summary = "Custom Rule Test / Calculator",
               description = "Evaluates arbitrary input parameters against scholarship rules without saving")
    public ResponseEntity<EligibilityCheckResponse> checkCustom(@RequestBody EligibilityCheckRequest request) {
        return ResponseEntity.ok(eligibilityService.evaluateEligibilityCustom(request));
    }

    @GetMapping("/student/{studentId}/matches")
    @Operation(summary = "Find Matching Scholarships for Student",
               description = "Scans all active scholarships and returns eligible opportunities ranked by match score")
    public ResponseEntity<List<EligibilityCheckResponse>> getMatchesForStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(eligibilityService.findEligibleScholarshipsForStudent(studentId));
    }
}
