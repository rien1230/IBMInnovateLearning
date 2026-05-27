package com.example.ibminnovate.controller;

import com.example.ibminnovate.model.ApiResponse;
import com.example.ibminnovate.model.Course;
import com.example.ibminnovate.model.LoginRequest;
import com.example.ibminnovate.model.User;
import com.example.ibminnovate.repo.CourseRepository;
import com.example.ibminnovate.service.LoginService;
import com.example.ibminnovate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DashboardController {

    @Autowired
    private CourseRepository courseRepository;



    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
    @GetMapping("/courses")
    public String showCourses() {
        return "courses";
    }

    @GetMapping("upcoming")
    public String showUpcoming() {
        return "upcoming-favourites";
    }

    @GetMapping("/course-duration/{courseId}")
    public ResponseEntity<String> getCourseDuration(@PathVariable Long courseId) {
        try {
            // Fetch the course from the database or in-memory list
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found"));

            // Return the course duration
            return ResponseEntity.ok(course.getDuration());
        } catch (Exception e) {
            System.err.println("Error in getCourseDuration: " + e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/leaderboard")
    public String showLeaderboard() { return "leaderboard"; }

}
