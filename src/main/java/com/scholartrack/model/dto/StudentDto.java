package com.scholartrack.model.dto;

import java.time.LocalDate;

public class StudentDto {
    private Long id;
    private Long userId;
    private String studentRollNo;
    private String fullName;
    private String email;
    private String phone;
    private String institutionName;
    private String departmentBranch;
    private String degreeLevel;
    private Integer currentYear;
    private Double gpaOrPercentage;
    private Double familyAnnualIncome;
    private String category;
    private String gender;
    private LocalDate dateOfBirth;
    private String bankAccountNo;
    private String bankIfsc;
    private String bankName;

    public StudentDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getStudentRollNo() { return studentRollNo; }
    public void setStudentRollNo(String studentRollNo) { this.studentRollNo = studentRollNo; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
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
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getBankAccountNo() { return bankAccountNo; }
    public void setBankAccountNo(String bankAccountNo) { this.bankAccountNo = bankAccountNo; }
    public String getBankIfsc() { return bankIfsc; }
    public void setBankIfsc(String bankIfsc) { this.bankIfsc = bankIfsc; }
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
}
