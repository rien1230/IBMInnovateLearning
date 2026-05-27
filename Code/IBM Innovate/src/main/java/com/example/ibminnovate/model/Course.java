package com.example.ibminnovate.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String courseName;
    @ElementCollection
    private List<String> tags;
    private String duration;
    @ElementCollection
    private List<String> languages;

    public Course() {
        super();
    }
    public Course(String courseName, List<String> tags, String duration, List<String> languages) {
        this.courseName = courseName;
        this.tags = tags;
        this.duration = duration;
        this.languages = languages;
    }
    // Getters and Setters

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseName() {
        return this.courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getDuration() {  // Fixed getter method
        return this.duration;
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
}
