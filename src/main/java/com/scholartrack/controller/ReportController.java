package com.scholartrack.controller;

import com.scholartrack.model.dto.ReportResultDto;
import com.scholartrack.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@Tag(name = "DBMS Reports", description = "Endpoints for executing advanced relational SQL analytical queries and reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    @Operation(summary = "List Available DBMS Reports", description = "Returns catalog of predefined SQL analytical queries")
    public ResponseEntity<List<Map<String, String>>> listReports() {
        return ResponseEntity.ok(reportService.getAvailableReports());
    }

    @GetMapping("/{reportId}")
    @Operation(summary = "Execute DBMS Report", description = "Executes specific SQL analytical query and returns structured columns and rows")
    public ResponseEntity<ReportResultDto> runReport(@PathVariable String reportId) {
        return ResponseEntity.ok(reportService.runReport(reportId));
    }
}
