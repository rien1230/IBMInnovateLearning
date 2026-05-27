package com.example.searchfilter;

import com.example.searchfilter.repository.CourseRepository;
import model.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import java.util.Arrays;
import java.util.List;

@EntityScan(basePackages = "model")
@SpringBootApplication
public class SearchFilterApplication implements CommandLineRunner {
    @Autowired
    private CourseRepository courseRepository;

    public static void main(String[] args) {
        SpringApplication.run(SearchFilterApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Course course = new Course();
        course.setCourseName("Java Basics");
        course.setTags(List.of("Java", "Programming"));
        course.setDuration("6 weeks");
        course.setLanguages("English");

        // Save the course to the repository
        courseRepository.save(course);
    }
}