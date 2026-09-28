package com.scholartrack.model.dto;

public class AuthResponse {
    private String token;
    private String username;
    private String role;
    private Long userId;
    private Long studentId;
    private String fullName;
    private String email;

    public AuthResponse() {}

    public AuthResponse(String token, String username, String role, Long userId, Long studentId, String fullName, String email) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.userId = userId;
        this.studentId = studentId;
        this.fullName = fullName;
        this.email = email;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
