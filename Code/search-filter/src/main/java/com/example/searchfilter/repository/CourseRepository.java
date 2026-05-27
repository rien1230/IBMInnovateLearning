package com.example.searchfilter.repository;

import model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findAll();

    //Search courses by name or tag
    List<Course> findByCourseNameContainingOrTagsContaining(String courseName, String tags);

    List<Course> findByDurationContaining(String duration);

    //Filter by multiple tags
    List<Course> findByTagsIn(List<String> tags);
}

