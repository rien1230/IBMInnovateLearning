package com.example.ibminnovate.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
public class Progress {

    @EmbeddedId
    private ProgressId id; // Composite key

    @ManyToOne
    @MapsId("userId") // Maps to ProgressId.userId
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("courseId") // Maps to ProgressId.courseId
    @JoinColumn(name = "course_id")
    private Course course;

    private String status; // e.g., "ongoing", "completed"

    private Timestamp startTime; // When the course was started
    private Timestamp endTime; // When the course was completed

    private int progressPercentage; // Optional: Track progress percentage (0-100)
    private int redoCount; // Optional: Track how many times the user has restarted the course

    // Constructors
    public Progress() {}

    public Progress(User user, Course course, String status, Timestamp startTime, Timestamp endTime, int progressPercentage, int redoCount) {
        this.id = new ProgressId(user.getID(), course.getId()); // Initialize composite key
        this.user = user;
        this.course = course;
        this.status = status;
        this.startTime = startTime;
        this.endTime = endTime;
        this.progressPercentage = progressPercentage;
        this.redoCount = redoCount;
    }

    // Getters and Setters
    public ProgressId getId() { return id; }
    public void setId(ProgressId id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getStartTime() { return startTime; }
    public void setStartTime(Timestamp startTime) { this.startTime = startTime; }

    public Timestamp getEndTime() { return endTime; }
    public void setEndTime(Timestamp endTime) { this.endTime = endTime; }

    public int getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(int progressPercentage) { this.progressPercentage = progressPercentage; }

    public int getRedoCount() { return redoCount; }
    public void setRedoCount(int redoCount) { this.redoCount = redoCount; }
}