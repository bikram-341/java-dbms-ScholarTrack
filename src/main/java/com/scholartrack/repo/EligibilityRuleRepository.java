package com.scholartrack.repo;

import com.scholartrack.model.EligibilityRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EligibilityRuleRepository extends JpaRepository<EligibilityRule, Long> {
    Optional<EligibilityRule> findByScholarshipId(Long scholarshipId);
}
