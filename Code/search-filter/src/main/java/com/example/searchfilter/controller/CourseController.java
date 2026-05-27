package com.example.searchfilter.controller;

import com.example.searchfilter.service.CourseService;
import model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
            @RequestParam(value = "tags", required = false) List<String> tags, Model model) {

        List<Course> filteredCourses = courseService.searchCourses(searchTerm, duration, tags);

        model.addAttribute("courses", filteredCourses);

        return "courses";
    }
}






