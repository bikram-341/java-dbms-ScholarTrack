package com.scholartrack.model.dto;

public class DashboardSummaryDto {
    private long totalScholarships;
    private long totalStudents;
    private long totalApplications;
    private long pendingVerifications;
    private long approvedApplications;
    private long rejectedApplications;
    private long disbursedApplications;
    private double totalDisbursedAmount;

    public DashboardSummaryDto() {}

    public long getTotalScholarships() { return totalScholarships; }
    public void setTotalScholarships(long totalScholarships) { this.totalScholarships = totalScholarships; }
    public long getTotalStudents() { return totalStudents; }
    public void setTotalStudents(long totalStudents) { this.totalStudents = totalStudents; }
    public long getTotalApplications() { return totalApplications; }
    public void setTotalApplications(long totalApplications) { this.totalApplications = totalApplications; }
    public long getPendingVerifications() { return pendingVerifications; }
    public void setPendingVerifications(long pendingVerifications) { this.pendingVerifications = pendingVerifications; }
    public long getApprovedApplications() { return approvedApplications; }
    public void setApprovedApplications(long approvedApplications) { this.approvedApplications = approvedApplications; }
    public long getRejectedApplications() { return rejectedApplications; }
    public void setRejectedApplications(long rejectedApplications) { this.rejectedApplications = rejectedApplications; }
    public long getDisbursedApplications() { return disbursedApplications; }
    public void setDisbursedApplications(long disbursedApplications) { this.disbursedApplications = disbursedApplications; }
    public double getTotalDisbursedAmount() { return totalDisbursedAmount; }
    public void setTotalDisbursedAmount(double totalDisbursedAmount) { this.totalDisbursedAmount = totalDisbursedAmount; }
}
