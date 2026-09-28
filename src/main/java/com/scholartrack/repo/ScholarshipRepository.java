package com.scholartrack.repo;

import com.scholartrack.model.ProviderType;
import com.scholartrack.model.Scholarship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScholarshipRepository extends JpaRepository<Scholarship, Long> {
    Optional<Scholarship> findByCode(String code);
    List<Scholarship> findByIsActiveTrue();
    Page<Scholarship> findByIsActiveTrue(Pageable pageable);
    List<Scholarship> findByProviderType(ProviderType providerType);
    Page<Scholarship> findByProviderType(ProviderType providerType, Pageable pageable);
    boolean existsByCode(String code);
}
