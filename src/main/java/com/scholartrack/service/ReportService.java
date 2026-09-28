package com.scholartrack.service;

import com.scholartrack.model.dto.ReportResultDto;
import java.util.List;
import java.util.Map;

public interface ReportService {
    List<Map<String, String>> getAvailableReports();
    ReportResultDto runReport(String reportId);
}
