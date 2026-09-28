package com.scholartrack.model.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReportResultDto {
    private String reportId;
    private String title;
    private String description;
    private List<String> columns = new ArrayList<>();
    private List<Map<String, Object>> rows = new ArrayList<>();

    public ReportResultDto() {}

    public ReportResultDto(String reportId, String title, String description, List<String> columns, List<Map<String, Object>> rows) {
        this.reportId = reportId;
        this.title = title;
        this.description = description;
        this.columns = columns;
        this.rows = rows;
    }

    public String getReportId() { return reportId; }
    public void setReportId(String reportId) { this.reportId = reportId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getColumns() { return columns; }
    public void setColumns(List<String> columns) { this.columns = columns; }
    public List<Map<String, Object>> getRows() { return rows; }
    public void setRows(List<Map<String, Object>> rows) { this.rows = rows; }
}
