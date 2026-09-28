package com.scholartrack.service;

import com.scholartrack.model.EligibilityRule;
import com.scholartrack.model.ProviderType;
import com.scholartrack.model.Scholarship;
import com.scholartrack.model.ScholarshipCategory;
import com.scholartrack.model.dto.PageResponse;
import com.scholartrack.model.dto.ScholarshipDto;
import com.scholartrack.repo.EligibilityRuleRepository;
import com.scholartrack.repo.ScholarshipRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScholarshipServiceImpl implements ScholarshipService {

    private final ScholarshipRepository scholarshipRepository;
    private final EligibilityRuleRepository eligibilityRuleRepository;

    public ScholarshipServiceImpl(ScholarshipRepository scholarshipRepository,
                                  EligibilityRuleRepository eligibilityRuleRepository) {
        this.scholarshipRepository = scholarshipRepository;
        this.eligibilityRuleRepository = eligibilityRuleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScholarshipDto> getAllActiveScholarships() {
        return scholarshipRepository.findByIsActiveTrue().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ScholarshipDto> getAllActiveScholarshipsPaged(ProviderType providerType, int page, int size, String sortBy, String direction) {
        Sort.Direction dir = "ASC".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String field = (sortBy != null && !sortBy.isBlank()) ? sortBy : "financialAidAmount";
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, field));

        Page<Scholarship> paged = (providerType != null) ?
                scholarshipRepository.findByProviderType(providerType, pageable) :
                scholarshipRepository.findByIsActiveTrue(pageable);

        List<ScholarshipDto> content = paged.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new PageResponse<>(content, paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScholarshipDto> getScholarshipsByProvider(ProviderType providerType) {
        return scholarshipRepository.findByProviderType(providerType).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ScholarshipDto getScholarshipById(Long id) {
        Scholarship s = scholarshipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Scholarship not found with ID: " + id));
        return mapToDto(s);
    }

    @Override
    @Transactional(readOnly = true)
    public ScholarshipDto getScholarshipByCode(String code) {
        Scholarship s = scholarshipRepository.findByCode(code)
                .orElseThrow(() -> new IllegalArgumentException("Scholarship not found with code: " + code));
        return mapToDto(s);
    }

    @Override
    @Transactional
    public ScholarshipDto createScholarship(ScholarshipDto dto) {
        if (scholarshipRepository.existsByCode(dto.getCode())) {
            throw new IllegalArgumentException("Scholarship code '" + dto.getCode() + "' already exists");
        }

        Scholarship s = new Scholarship();
        s.setCode(dto.getCode());
        s.setTitle(dto.getTitle());
        s.setDescription(dto.getDescription());
        s.setProviderType(ProviderType.valueOf(dto.getProviderType()));
        s.setScholarshipCategory(ScholarshipCategory.valueOf(dto.getScholarshipCategory()));
        s.setFinancialAidAmount(dto.getFinancialAidAmount());
        s.setTotalSlots(dto.getTotalSlots());
        s.setSlotsRemaining(dto.getSlotsRemaining() != null ? dto.getSlotsRemaining() : dto.getTotalSlots());
        s.setApplicationDeadline(dto.getApplicationDeadline() != null ? dto.getApplicationDeadline() : LocalDate.now().plusMonths(3));
        s.setAcademicYear(dto.getAcademicYear() != null ? dto.getAcademicYear() : "2026-2027");
        s.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);

        Scholarship saved = scholarshipRepository.save(s);

        EligibilityRule rule = new EligibilityRule();
        rule.setScholarship(saved);
        rule.setMinGpaOrPercentage(dto.getMinGpaOrPercentage() != null ? dto.getMinGpaOrPercentage() : 6.0);
        rule.setMaxFamilyIncome(dto.getMaxFamilyIncome() != null ? dto.getMaxFamilyIncome() : 500000.0);
        rule.setEligibleCategories(dto.getEligibleCategories() != null ? dto.getEligibleCategories() : "ALL");
        rule.setEligibleDegrees(dto.getEligibleDegrees() != null ? dto.getEligibleDegrees() : "ALL");
        rule.setEligibleGender(dto.getEligibleGender() != null ? dto.getEligibleGender() : "ANY");
        rule.setMinAge(dto.getMinAge() != null ? dto.getMinAge() : 16);
        rule.setMaxAge(dto.getMaxAge() != null ? dto.getMaxAge() : 35);
        rule.setRequiredDocuments(dto.getRequiredDocuments() != null ? dto.getRequiredDocuments() : "INCOME_CERTIFICATE,GRADE_MARKSHEET");
        rule.setRuleDescription(dto.getRuleDescription() != null ? dto.getRuleDescription() : "Standard eligibility criteria");
        saved.setEligibilityRule(rule);
        eligibilityRuleRepository.save(rule);

        return mapToDto(saved);
    }

    @Override
    @Transactional
    public ScholarshipDto updateScholarship(Long id, ScholarshipDto dto) {
        Scholarship s = scholarshipRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Scholarship not found with ID: " + id));

        if (dto.getTitle() != null) s.setTitle(dto.getTitle());
        if (dto.getDescription() != null) s.setDescription(dto.getDescription());
        if (dto.getFinancialAidAmount() != null) s.setFinancialAidAmount(dto.getFinancialAidAmount());
        if (dto.getTotalSlots() != null) s.setTotalSlots(dto.getTotalSlots());
        if (dto.getSlotsRemaining() != null) s.setSlotsRemaining(dto.getSlotsRemaining());
        if (dto.getApplicationDeadline() != null) s.setApplicationDeadline(dto.getApplicationDeadline());
        if (dto.getIsActive() != null) s.setIsActive(dto.getIsActive());

        if (s.getEligibilityRule() != null) {
            EligibilityRule rule = s.getEligibilityRule();
            if (dto.getMinGpaOrPercentage() != null) rule.setMinGpaOrPercentage(dto.getMinGpaOrPercentage());
            if (dto.getMaxFamilyIncome() != null) rule.setMaxFamilyIncome(dto.getMaxFamilyIncome());
            if (dto.getEligibleCategories() != null) rule.setEligibleCategories(dto.getEligibleCategories());
            if (dto.getEligibleDegrees() != null) rule.setEligibleDegrees(dto.getEligibleDegrees());
            if (dto.getEligibleGender() != null) rule.setEligibleGender(dto.getEligibleGender());
            if (dto.getRequiredDocuments() != null) rule.setRequiredDocuments(dto.getRequiredDocuments());
            if (dto.getRuleDescription() != null) rule.setRuleDescription(dto.getRuleDescription());
            eligibilityRuleRepository.save(rule);
        }

        return mapToDto(scholarshipRepository.save(s));
    }

    @Override
    @Transactional
    public void deleteScholarship(Long id) {
        scholarshipRepository.deleteById(id);
    }

    private ScholarshipDto mapToDto(Scholarship s) {
        ScholarshipDto dto = new ScholarshipDto();
        dto.setId(s.getId());
        dto.setCode(s.getCode());
        dto.setTitle(s.getTitle());
        dto.setDescription(s.getDescription());
        dto.setProviderType(s.getProviderType().name());
        dto.setScholarshipCategory(s.getScholarshipCategory().name());
        dto.setFinancialAidAmount(s.getFinancialAidAmount());
        dto.setTotalSlots(s.getTotalSlots());
        dto.setSlotsRemaining(s.getSlotsRemaining());
        dto.setApplicationDeadline(s.getApplicationDeadline());
        dto.setAcademicYear(s.getAcademicYear());
        dto.setIsActive(s.getIsActive());

        EligibilityRule r = s.getEligibilityRule();
        if (r != null) {
            dto.setMinGpaOrPercentage(r.getMinGpaOrPercentage());
            dto.setMaxFamilyIncome(r.getMaxFamilyIncome());
            dto.setEligibleCategories(r.getEligibleCategories());
            dto.setEligibleDegrees(r.getEligibleDegrees());
            dto.setEligibleGender(r.getEligibleGender());
            dto.setMinAge(r.getMinAge());
            dto.setMaxAge(r.getMaxAge());
            dto.setRequiredDocuments(r.getRequiredDocuments());
            dto.setRuleDescription(r.getRuleDescription());
        }
        return dto;
    }
}
