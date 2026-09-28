package com.scholartrack.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String fullName;
    private String phone;
    private String role; // default ROLE_STUDENT if empty

    // Optional student attributes if registering as student
    private String studentRollNo;
    private String institutionName;
    private String departmentBranch;
    private String degreeLevel;
    private Integer currentYear;
    private Double gpaOrPercentage;
    private Double familyAnnualIncome;
    private String category;
    private String gender;

    public RegisterRequest() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStudentRollNo() { return studentRollNo; }
    public void setStudentRollNo(String studentRollNo) { this.studentRollNo = studentRollNo; }
    public String getInstitutionName() { return institutionName; }
    public void setInstitutionName(String institutionName) { this.institutionName = institutionName; }
    public String getDepartmentBranch() { return departmentBranch; }
    public void setDepartmentBranch(String departmentBranch) { this.departmentBranch = departmentBranch; }
    public String getDegreeLevel() { return degreeLevel; }
    public void setDegreeLevel(String degreeLevel) { this.degreeLevel = degreeLevel; }
    public Integer getCurrentYear() { return currentYear; }
    public void setCurrentYear(Integer currentYear) { this.currentYear = currentYear; }
    public Double getGpaOrPercentage() { return gpaOrPercentage; }
    public void setGpaOrPercentage(Double gpaOrPercentage) { this.gpaOrPercentage = gpaOrPercentage; }
    public Double getFamilyAnnualIncome() { return familyAnnualIncome; }
    public void setFamilyAnnualIncome(Double familyAnnualIncome) { this.familyAnnualIncome = familyAnnualIncome; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
}
