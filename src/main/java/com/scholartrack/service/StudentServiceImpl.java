package com.scholartrack.service;

import com.scholartrack.model.DegreeLevel;
import com.scholartrack.model.Student;
import com.scholartrack.model.StudentCategory;
import com.scholartrack.model.dto.PageResponse;
import com.scholartrack.model.dto.StudentDto;
import com.scholartrack.repo.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<StudentDto> getAllStudentsPaged(int page, int size, String sortBy, String direction) {
        Sort.Direction dir = "ASC".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String field = (sortBy != null && !sortBy.isBlank()) ? sortBy : "id";
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, field));

        Page<Student> paged = studentRepository.findAll(pageable);
        List<StudentDto> content = paged.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new PageResponse<>(content, paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isFirst(), paged.isLast());
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDto getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + id));
        return mapToDto(student);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDto getStudentByUserId(Long userId) {
        Student student = studentRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Student profile not found for User ID: " + userId));
        return mapToDto(student);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDto> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StudentDto updateStudent(Long id, StudentDto dto) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found with ID: " + id));

        if (dto.getFullName() != null) student.setFullName(dto.getFullName());
        if (dto.getPhone() != null) student.setPhone(dto.getPhone());
        if (dto.getInstitutionName() != null) student.setInstitutionName(dto.getInstitutionName());
        if (dto.getDepartmentBranch() != null) student.setDepartmentBranch(dto.getDepartmentBranch());
        if (dto.getCurrentYear() != null) student.setCurrentYear(dto.getCurrentYear());
        if (dto.getGpaOrPercentage() != null) student.setGpaOrPercentage(dto.getGpaOrPercentage());
        if (dto.getFamilyAnnualIncome() != null) student.setFamilyAnnualIncome(dto.getFamilyAnnualIncome());
        if (dto.getGender() != null) student.setGender(dto.getGender());
        if (dto.getDateOfBirth() != null) student.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getBankAccountNo() != null) student.setBankAccountNo(dto.getBankAccountNo());
        if (dto.getBankIfsc() != null) student.setBankIfsc(dto.getBankIfsc());
        if (dto.getBankName() != null) student.setBankName(dto.getBankName());

        if (dto.getDegreeLevel() != null) {
            try {
                student.setDegreeLevel(DegreeLevel.valueOf(dto.getDegreeLevel()));
            } catch (Exception ignored) {}
        }

        if (dto.getCategory() != null) {
            try {
                student.setCategory(StudentCategory.valueOf(dto.getCategory()));
            } catch (Exception ignored) {}
        }

        return mapToDto(studentRepository.save(student));
    }

    private StudentDto mapToDto(Student s) {
        StudentDto dto = new StudentDto();
        dto.setId(s.getId());
        dto.setUserId(s.getUser() != null ? s.getUser().getId() : null);
        dto.setStudentRollNo(s.getStudentRollNo());
        dto.setFullName(s.getFullName());
        dto.setEmail(s.getEmail());
        dto.setPhone(s.getPhone());
        dto.setInstitutionName(s.getInstitutionName());
        dto.setDepartmentBranch(s.getDepartmentBranch());
        dto.setDegreeLevel(s.getDegreeLevel() != null ? s.getDegreeLevel().name() : null);
        dto.setCurrentYear(s.getCurrentYear());
        dto.setGpaOrPercentage(s.getGpaOrPercentage());
        dto.setFamilyAnnualIncome(s.getFamilyAnnualIncome());
        dto.setCategory(s.getCategory() != null ? s.getCategory().name() : null);
        dto.setGender(s.getGender());
        dto.setDateOfBirth(s.getDateOfBirth());
        dto.setBankAccountNo(s.getBankAccountNo());
        dto.setBankIfsc(s.getBankIfsc());
        dto.setBankName(s.getBankName());
        return dto;
    }
}
