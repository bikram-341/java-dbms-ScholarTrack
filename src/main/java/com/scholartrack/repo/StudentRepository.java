package com.scholartrack.repo;

import com.scholartrack.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUserId(Long userId);
    Optional<Student> findByStudentRollNo(String studentRollNo);
    Optional<Student> findByEmail(String email);
    boolean existsByStudentRollNo(String studentRollNo);
    boolean existsByEmail(String email);
}
