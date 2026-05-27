package com.example.ibminnovate.dto;

import java.sql.Timestamp;
import java.util.List;

public class UserCourseDTO {
    private Long courseId;
    private String courseName;
    private Timestamp startTime;
    private Timestamp endTime;
    private String duration;
    private List<String> languages;
    private int redoCount;

    // Getters and Setters
    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public int getRedoCount() {
        return redoCount;
    }

    public void setRedoCount(int redoCount) {
        this.redoCount = redoCount;
    }
}
