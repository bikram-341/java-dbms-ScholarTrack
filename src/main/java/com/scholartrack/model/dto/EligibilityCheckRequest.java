package com.scholartrack.model.dto;

public class EligibilityCheckRequest {
    private Long scholarshipId; // optional, if null checks against all scholarships
    private Long studentId;     // optional, if provided fills below fields from profile
    private Double gpaOrPercentage;
    private Double familyAnnualIncome;
    private String category;    // GENERAL, OBC, SC, ST, EWS
    private String degreeLevel; // UNDERGRADUATE, POSTGRADUATE, DIPLOMA, PHD
    private String gender;      // MALE, FEMALE, OTHER
    private Integer age;

    public EligibilityCheckRequest() {}

    public Long getScholarshipId() { return scholarshipId; }
    public void setScholarshipId(Long scholarshipId) { this.scholarshipId = scholarshipId; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Double getGpaOrPercentage() { return gpaOrPercentage; }
    public void setGpaOrPercentage(Double gpaOrPercentage) { this.gpaOrPercentage = gpaOrPercentage; }
    public Double getFamilyAnnualIncome() { return familyAnnualIncome; }
    public void setFamilyAnnualIncome(Double familyAnnualIncome) { this.familyAnnualIncome = familyAnnualIncome; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDegreeLevel() { return degreeLevel; }
    public void setDegreeLevel(String degreeLevel) { this.degreeLevel = degreeLevel; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
}
