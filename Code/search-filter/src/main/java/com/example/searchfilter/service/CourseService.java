package com.example.searchfilter.service;

import com.example.searchfilter.repository.CourseRepository;
import model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepo;

    public List<Course> searchCourses(String searchTerm, String duration, List<String> tags) {
        if (tags != null && !tags.isEmpty()) {
            return courseRepo.findByTagsIn(tags);
        } else if (duration != null && !duration.isEmpty()) {
            return courseRepo.findByDurationContaining(duration);
        } else if (searchTerm != null && !searchTerm.isEmpty()) {
            return courseRepo.findByCourseNameContainingOrTagsContaining(searchTerm, searchTerm);
        } else {
            //If no specific criteria is given, return all courses
            return courseRepo.findAll();
        }
    }
}
