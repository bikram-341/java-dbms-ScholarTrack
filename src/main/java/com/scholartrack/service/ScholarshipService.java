package com.scholartrack.service;

import com.scholartrack.model.ProviderType;
import com.scholartrack.model.dto.ScholarshipDto;

import com.scholartrack.model.dto.PageResponse;

import java.util.List;

public interface ScholarshipService {
    List<ScholarshipDto> getAllActiveScholarships();
    PageResponse<ScholarshipDto> getAllActiveScholarshipsPaged(ProviderType providerType, int page, int size, String sortBy, String direction);
    List<ScholarshipDto> getScholarshipsByProvider(ProviderType providerType);
    ScholarshipDto getScholarshipById(Long id);
    ScholarshipDto getScholarshipByCode(String code);
    ScholarshipDto createScholarship(ScholarshipDto dto);
    ScholarshipDto updateScholarship(Long id, ScholarshipDto dto);
    void deleteScholarship(Long id);
}
