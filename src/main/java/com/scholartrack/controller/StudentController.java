package com.scholartrack.controller;

import com.scholartrack.model.dto.PageResponse;
import com.scholartrack.model.dto.StudentDto;
import com.scholartrack.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Students", description = "Endpoints for managing student academic, economic, and banking profiles")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Student Profile", description = "Retrieves student record by student ID")
    public ResponseEntity<StudentDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get Student by User ID", description = "Retrieves student profile linked to logged-in user")
    public ResponseEntity<StudentDto> getByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(studentService.getStudentByUserId(userId));
    }

    @GetMapping
    @Operation(summary = "List All Students", description = "Retrieves roster of registered students")
    public ResponseEntity<List<StudentDto>> getAll() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/paged")
    @Operation(summary = "List Students Paged & Sorted", description = "Retrieves paginated and sorted roster of registered students")
    public ResponseEntity<PageResponse<StudentDto>> getAllPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        return ResponseEntity.ok(studentService.getAllStudentsPaged(page, size, sortBy, direction));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Student Profile", description = "Updates academic score, income, banking or contact info")
    public ResponseEntity<StudentDto> update(@PathVariable Long id, @RequestBody StudentDto dto) {
        return ResponseEntity.ok(studentService.updateStudent(id, dto));
    }
}
