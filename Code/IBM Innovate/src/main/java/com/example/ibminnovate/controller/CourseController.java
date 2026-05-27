package com.example.ibminnovate.controller;

import com.example.ibminnovate.dto.StartCourseRequest;
import com.example.ibminnovate.model.Course;
import com.example.ibminnovate.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/search")
    public String searchCourses(
            @RequestParam(value = "searchTerm", defaultValue = "") String searchTerm,
            @RequestParam(value = "duration", defaultValue = "") String duration,
            @RequestParam(value = "languages", required = false) List<String> languages,
            @RequestParam(value = "tags", required = false) List<String> tags, Model model) {

        List<Course> filteredCourses = courseService.searchCourses(searchTerm, duration, languages, tags);
        model.addAttribute("courses", filteredCourses);
        return "courses";
    }

    @PostMapping("/update-details")
    public ResponseEntity<String> updateCourseDetails(@RequestBody StartCourseRequest request) {
        try {
            courseService.updateCourseDetails(request.getCourseId(), request.getCourseName(), request.getTags(), request.getDuration(), request.getLanguages());
            return ResponseEntity.ok("Course details updated successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update course details: " + e.getMessage());
        }
    }
}

