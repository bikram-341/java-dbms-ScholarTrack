package com.scholartrack.service;

import com.scholartrack.model.Student;
import com.scholartrack.model.dto.EligibilityCheckRequest;
import com.scholartrack.model.dto.EligibilityCheckResponse;

import java.util.List;

public interface EligibilityService {
    EligibilityCheckResponse evaluateEligibility(Long scholarshipId, Student student);
    EligibilityCheckResponse evaluateEligibilityCustom(EligibilityCheckRequest request);
    List<EligibilityCheckResponse> findEligibleScholarshipsForStudent(Long studentId);
}
