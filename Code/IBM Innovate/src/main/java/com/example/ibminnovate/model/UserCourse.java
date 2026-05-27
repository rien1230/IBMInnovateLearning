package com.example.ibminnovate.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
public class UserCourse {

    @EmbeddedId
    private UserCourseId id;

    @ManyToOne
    @MapsId("userId")  // Maps to UserCourseId.userId
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("courseId")  // Maps to UserCourseId.courseId
    @JoinColumn(name = "course_id")
    private Course course;

    private Timestamp startTime;
    private Timestamp pauseTime;
    private Timestamp endTime;
    private String status;  // e.g., "enrolled", "completed"
    private int redoCount;  // Tracks how many times the user has rejoined

    // Constructors
    public UserCourse() {}

    public UserCourse(User user, Course course, Timestamp startTime, Timestamp endTime, String status, int redoCount, Timestamp pauseTime) {
        this.id = new UserCourseId(user.getID(), course.getId());  // Initialize composite key
        this.user = user;
        this.course = course;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.redoCount = redoCount;
        this.pauseTime = pauseTime;
    }

    // Getters and Setters
    public UserCourseId getId() { return id; }
    public void setId(UserCourseId id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public Timestamp getStartTime() { return startTime; }
    public void setStartTime(Timestamp startTime) { this.startTime = startTime; }

    public Timestamp getEndTime() { return endTime; }
    public void setEndTime(Timestamp endTime) { this.endTime = endTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getRedoCount() { return redoCount; }
    public void setRedoCount(int redoCount) { this.redoCount = redoCount; }

    public Timestamp getPauseTime() { return pauseTime; }
    public void setPauseTime(Timestamp pauseTime) { this.pauseTime = pauseTime; }
}
