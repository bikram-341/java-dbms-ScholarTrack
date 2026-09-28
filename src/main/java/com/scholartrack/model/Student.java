package com.scholartrack.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(name = "student_roll_no", nullable = false, unique = true, length = 50)
    private String studentRollNo;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 25)
    private String phone;

    @Column(name = "institution_name", nullable = false, length = 150)
    private String institutionName;

    @Column(name = "department_branch", nullable = false, length = 100)
    private String departmentBranch;

    @Enumerated(EnumType.STRING)
    @Column(name = "degree_level", nullable = false, length = 50)
    private DegreeLevel degreeLevel;

    @Column(name = "current_year", nullable = false)
    private Integer currentYear;

    @Column(name = "gpa_or_percentage", nullable = false)
    private Double gpaOrPercentage;

    @Column(name = "family_annual_income", nullable = false)
    private Double familyAnnualIncome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StudentCategory category;

    @Column(nullable = false, length = 20)
    private String gender; // MALE, FEMALE, OTHER

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "bank_account_no", length = 50)
    private String bankAccountNo;

    @Column(name = "bank_ifsc", length = 30)
    private String bankIfsc;

    @Column(name = "bank_name", length = 100)
    private String bankName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Student() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
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
    public DegreeLevel getDegreeLevel() { return degreeLevel; }
    public void setDegreeLevel(DegreeLevel degreeLevel) { this.degreeLevel = degreeLevel; }
    public Integer getCurrentYear() { return currentYear; }
    public void setCurrentYear(Integer currentYear) { this.currentYear = currentYear; }
    public Double getGpaOrPercentage() { return gpaOrPercentage; }
    public void setGpaOrPercentage(Double gpaOrPercentage) { this.gpaOrPercentage = gpaOrPercentage; }
    public Double getFamilyAnnualIncome() { return familyAnnualIncome; }
    public void setFamilyAnnualIncome(Double familyAnnualIncome) { this.familyAnnualIncome = familyAnnualIncome; }
    public StudentCategory getCategory() { return category; }
    public void setCategory(StudentCategory category) { this.category = category; }
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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
