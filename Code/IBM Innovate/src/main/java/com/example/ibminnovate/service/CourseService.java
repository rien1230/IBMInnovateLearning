package com.example.ibminnovate.service;

import com.example.ibminnovate.model.Course;
import com.example.ibminnovate.repo.CourseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    private final CourseRepository courseRepo;

    @Autowired
    public CourseService(CourseRepository courseRepository) {
        this.courseRepo = courseRepository;
    }

    public List<Course> searchCourses(String searchTerm, String duration, List<String> languages, List<String> tags) {
        if (tags != null && !tags.isEmpty()) {
            return courseRepo.findByTagsIn(tags);
        } else if (languages != null && !languages.isEmpty()) {
            return courseRepo.findByLanguagesIn(languages);
        } else if (duration != null && !duration.isEmpty()) {
            return courseRepo.findByDurationContaining(duration);
        } else if (searchTerm != null && !searchTerm.isEmpty()) {
            return courseRepo.findByCourseNameContainingOrTagsContaining(searchTerm, searchTerm);
        } else {
            //If no specific criteria is given, return all courses
            return courseRepo.findAll();
        }
    }
    @Transactional
    public void updateCourseDetails(Long courseId, String courseName, List<String> tags, String duration, List<String> languages) {
        // Find the course by ID
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Update the course details
        course.setCourseName(courseName);
        course.setTags(tags);
        course.setDuration(duration);
        course.setLanguages(languages);

        // Save the updated course
        courseRepo.save(course);
    }
}
