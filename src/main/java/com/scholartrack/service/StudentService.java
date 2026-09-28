package com.scholartrack.service;

import com.scholartrack.model.dto.PageResponse;
import com.scholartrack.model.dto.StudentDto;
import java.util.List;

public interface StudentService {
    StudentDto getStudentById(Long id);
    StudentDto getStudentByUserId(Long userId);
    StudentDto updateStudent(Long id, StudentDto dto);
    List<StudentDto> getAllStudents();
    PageResponse<StudentDto> getAllStudentsPaged(int page, int size, String sortBy, String direction);
}
