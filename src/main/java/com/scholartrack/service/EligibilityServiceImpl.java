package com.scholartrack.service;

import com.scholartrack.model.EligibilityRule;
import com.scholartrack.model.Scholarship;
import com.scholartrack.model.Student;
import com.scholartrack.model.dto.EligibilityCheckRequest;
import com.scholartrack.model.dto.EligibilityCheckResponse;
import com.scholartrack.repo.ScholarshipRepository;
import com.scholartrack.repo.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class EligibilityServiceImpl implements EligibilityService {

    private final ScholarshipRepository scholarshipRepository;
    private final StudentRepository studentRepository;

    public EligibilityServiceImpl(ScholarshipRepository scholarshipRepository,
                                  StudentRepository studentRepository) {
        this.scholarshipRepository = scholarshipRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public EligibilityCheckResponse evaluateEligibility(Long scholarshipId, Student student) {
        Scholarship scholarship = scholarshipRepository.findById(scholarshipId)
                .orElseThrow(() -> new IllegalArgumentException("Scholarship not found: " + scholarshipId));

        int age = 20;
        if (student.getDateOfBirth() != null) {
            age = Period.between(student.getDateOfBirth(), LocalDate.now()).getYears();
        }

        return checkRules(
                scholarship,
                student.getGpaOrPercentage(),
                student.getFamilyAnnualIncome(),
                student.getCategory() != null ? student.getCategory().name() : "GENERAL",
                student.getDegreeLevel() != null ? student.getDegreeLevel().name() : "UNDERGRADUATE",
                student.getGender(),
                age
        );
    }

    @Override
    @Transactional(readOnly = true)
    public EligibilityCheckResponse evaluateEligibilityCustom(EligibilityCheckRequest request) {
        if (request.getStudentId() != null) {
            Student s = studentRepository.findById(request.getStudentId()).orElse(null);
            if (s != null) {
                if (request.getGpaOrPercentage() == null) request.setGpaOrPercentage(s.getGpaOrPercentage());
                if (request.getFamilyAnnualIncome() == null) request.setFamilyAnnualIncome(s.getFamilyAnnualIncome());
                if (request.getCategory() == null && s.getCategory() != null) request.setCategory(s.getCategory().name());
                if (request.getDegreeLevel() == null && s.getDegreeLevel() != null) request.setDegreeLevel(s.getDegreeLevel().name());
                if (request.getGender() == null) request.setGender(s.getGender());
                if (request.getAge() == null && s.getDateOfBirth() != null) {
                    request.setAge(Period.between(s.getDateOfBirth(), LocalDate.now()).getYears());
                }
            }
        }

        Scholarship scholarship = scholarshipRepository.findById(request.getScholarshipId())
                .orElseThrow(() -> new IllegalArgumentException("Scholarship not found: " + request.getScholarshipId()));

        return checkRules(
                scholarship,
                request.getGpaOrPercentage() != null ? request.getGpaOrPercentage() : 0.0,
                request.getFamilyAnnualIncome() != null ? request.getFamilyAnnualIncome() : Double.MAX_VALUE,
                request.getCategory() != null ? request.getCategory() : "GENERAL",
                request.getDegreeLevel() != null ? request.getDegreeLevel() : "UNDERGRADUATE",
                request.getGender() != null ? request.getGender() : "OTHER",
                request.getAge() != null ? request.getAge() : 20
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<EligibilityCheckResponse> findEligibleScholarshipsForStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

        List<Scholarship> activeList = scholarshipRepository.findByIsActiveTrue();
        List<EligibilityCheckResponse> results = new ArrayList<>();

        for (Scholarship sc : activeList) {
            results.add(evaluateEligibility(sc.getId(), student));
        }

        // Sort: eligible ones first, then by score descending
        results.sort((a, b) -> {
            if (a.isEligible() != b.isEligible()) {
                return a.isEligible() ? -1 : 1;
            }
            return Double.compare(b.getScore(), a.getScore());
        });

        return results;
    }

    private EligibilityCheckResponse checkRules(Scholarship scholarship,
                                                Double gpa,
                                                Double income,
                                                String category,
                                                String degreeLevel,
                                                String gender,
                                                Integer age) {
        EligibilityCheckResponse resp = new EligibilityCheckResponse();
        resp.setScholarshipId(scholarship.getId());
        resp.setScholarshipCode(scholarship.getCode());
        resp.setScholarshipTitle(scholarship.getTitle());
        resp.setProviderType(scholarship.getProviderType().name());
        resp.setFinancialAidAmount(scholarship.getFinancialAidAmount());

        EligibilityRule rule = scholarship.getEligibilityRule();
        if (rule == null) {
            resp.setEligible(true);
            resp.setScore(80.0);
            resp.getPassedCriteria().add("Open scholarship without restrictive constraints.");
            resp.setRecommendationVerdict("ELIGIBLE - All candidates may apply.");
            return resp;
        }

        List<String> passed = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        // 1. GPA check
        if (gpa != null && gpa >= rule.getMinGpaOrPercentage()) {
            passed.add("Academic Criterion: GPA/Score " + gpa + " satisfies minimum required " + rule.getMinGpaOrPercentage());
        } else {
            failed.add("Academic Deficit: GPA/Score " + (gpa != null ? gpa : "N/A") + " is below minimum required " + rule.getMinGpaOrPercentage());
        }

        // 2. Family Income check
        if (income != null && income <= rule.getMaxFamilyIncome()) {
            passed.add("Income Ceiling: Annual family income ₹" + income.longValue() + " is within upper limit ₹" + rule.getMaxFamilyIncome().longValue());
        } else {
            failed.add("Income Exceeded: Annual family income ₹" + (income != null ? income.longValue() : "N/A") + " exceeds ceiling ₹" + rule.getMaxFamilyIncome().longValue());
        }

        // 3. Category check
        if ("ALL".equalsIgnoreCase(rule.getEligibleCategories()) || rule.getEligibleCategories() == null) {
            passed.add("Social Category: Open to all reservation and general categories.");
        } else {
            List<String> allowedCats = Arrays.asList(rule.getEligibleCategories().toUpperCase().split(","));
            if (category != null && allowedCats.contains(category.toUpperCase().trim())) {
                passed.add("Social Category: Candidate category (" + category + ") is eligible.");
            } else {
                failed.add("Social Category: Reserved for (" + rule.getEligibleCategories() + "), but student is (" + category + ").");
            }
        }

        // 4. Degree check
        if ("ALL".equalsIgnoreCase(rule.getEligibleDegrees()) || rule.getEligibleDegrees() == null) {
            passed.add("Academic Level: Open to all degree programs.");
        } else {
            List<String> allowedDegrees = Arrays.asList(rule.getEligibleDegrees().toUpperCase().split(","));
            if (degreeLevel != null && allowedDegrees.contains(degreeLevel.toUpperCase().trim())) {
                passed.add("Academic Level: Enrolled degree (" + degreeLevel + ") is supported.");
            } else {
                failed.add("Academic Level: Applicable only for (" + rule.getEligibleDegrees() + "), current: (" + degreeLevel + ").");
            }
        }

        // 5. Gender check
        if ("ANY".equalsIgnoreCase(rule.getEligibleGender()) || rule.getEligibleGender() == null) {
            passed.add("Gender: Open to all applicants.");
        } else {
            if (gender != null && rule.getEligibleGender().equalsIgnoreCase(gender.trim())) {
                passed.add("Gender: Target criteria (" + rule.getEligibleGender() + ") fulfilled.");
            } else {
                failed.add("Gender Mismatch: Fellowship restricted to (" + rule.getEligibleGender() + "), applicant is (" + gender + ").");
            }
        }

        // 6. Age check
        if (rule.getMinAge() != null && rule.getMaxAge() != null && age != null) {
            if (age >= rule.getMinAge() && age <= rule.getMaxAge()) {
                passed.add("Age Limit: Age " + age + " is within permissible bracket [" + rule.getMinAge() + " - " + rule.getMaxAge() + "].");
            } else {
                failed.add("Age Limit: Age " + age + " is outside eligible bracket [" + rule.getMinAge() + " - " + rule.getMaxAge() + "].");
            }
        }

        // Required Documents
        if (rule.getRequiredDocuments() != null) {
            String[] docs = rule.getRequiredDocuments().split(",");
            for (String d : docs) {
                if (!d.trim().isEmpty()) {
                    resp.getRequiredDocuments().add(d.trim());
                }
            }
        }

        boolean isEligible = failed.isEmpty();
        resp.setEligible(isEligible);
        resp.setPassedCriteria(passed);
        resp.setFailedCriteria(failed);

        // Calculate score
        double baseScore = (gpa != null ? Math.min(100.0, gpa * 10.0) : 60.0);
        double needBonus = 0.0;
        if (income != null) {
            if (income < 200000.0) needBonus = 15.0;
            else if (income < 400000.0) needBonus = 10.0;
            else if (income < 600000.0) needBonus = 5.0;
        }
        double finalScore = Math.min(100.0, Math.round((baseScore * 0.7 + needBonus * 2.0) * 10.0) / 10.0);
        resp.setScore(finalScore);

        if (isEligible) {
            resp.setRecommendationVerdict("ELIGIBLE - High probability of award. Prepare mandatory documents for submission.");
        } else {
            resp.setRecommendationVerdict("INELIGIBLE - Criteria requirement(s) unmet. See failed conditions above.");
        }

        return resp;
    }
}
