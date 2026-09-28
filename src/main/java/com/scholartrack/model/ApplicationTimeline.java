package com.scholartrack.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "application_timelines")
public class ApplicationTimeline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    @JsonBackReference
    private ScholarshipApplication application;

    @Column(name = "stage_name", nullable = false, length = 100)
    private String stageName;

    @Column(nullable = false, length = 50)
    private String status; // COMPLETED, IN_PROGRESS, REJECTED, PENDING

    @Column(length = 500)
    private String comments;

    @Column(name = "updated_by_name", length = 100)
    private String updatedByName;

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp = LocalDateTime.now();

    public ApplicationTimeline() {}

    public ApplicationTimeline(ScholarshipApplication application, String stageName, String status, String comments, String updatedByName) {
        this.application = application;
        this.stageName = stageName;
        this.status = status;
        this.comments = comments;
        this.updatedByName = updatedByName;
        this.timestamp = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ScholarshipApplication getApplication() { return application; }
    public void setApplication(ScholarshipApplication application) { this.application = application; }
    public String getStageName() { return stageName; }
    public void setStageName(String stageName) { this.stageName = stageName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    public String getUpdatedByName() { return updatedByName; }
    public void setUpdatedByName(String updatedByName) { this.updatedByName = updatedByName; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
