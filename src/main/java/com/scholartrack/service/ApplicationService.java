package com.scholartrack.service;

import com.scholartrack.model.ApplicationStatus;
import com.scholartrack.model.dto.*;

import java.util.List;

public interface ApplicationService {
    ApplicationResponse apply(ApplicationRequest request);
    ApplicationResponse getApplicationById(Long id);
    ApplicationResponse getApplicationByNumber(String applicationNumber);
    List<ApplicationResponse> getApplicationsByStudent(Long studentId);
    List<ApplicationResponse> getAllApplications(ApplicationStatus statusFilter);
    PageResponse<ApplicationResponse> getAllApplicationsPaged(ApplicationStatus statusFilter, int page, int size, String sortBy, String direction);
    ApplicationResponse makeDecision(ApplicationDecisionRequest request);
    ApplicationResponse disburse(DisbursementRequest request);
    List<ApplicationResponse> simulateDataGrowth(int count);
}
