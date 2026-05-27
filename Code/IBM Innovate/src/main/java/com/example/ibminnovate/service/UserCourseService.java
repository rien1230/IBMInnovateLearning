package com.example.ibminnovate.service;


import com.example.ibminnovate.model.Course;
import com.example.ibminnovate.model.User;
import com.example.ibminnovate.model.UserCourse;
import com.example.ibminnovate.model.UserCourseId;
import com.example.ibminnovate.repo.UserCourseRepository;
import com.example.ibminnovate.repo.UserRepository;
import com.example.ibminnovate.repo.CourseRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class UserCourseService {

    private final UserCourseRepository userCourseRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public UserCourseService(UserCourseRepository userCourseRepository,
                             UserRepository userRepository,
                             CourseRepository courseRepository) {
        this.userCourseRepository = userCourseRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    public String addToUpcoming(Long userId, Long courseId) {
        // Check if the user and course exist
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // Check if the course is already in the user's upcoming list
        if (userCourseRepository.existsByUserIDAndCourseIdAndStatus(userId, courseId, "upcoming")) {
            return "Course is already in your upcoming list.";
        }

        // Create a new UserCourse entry
        UserCourse userCourse = new UserCourse();
        userCourse.setId(new UserCourseId(userId, courseId)); // Set the composite key
        userCourse.setUser(user);
        userCourse.setCourse(course);
        userCourse.setStartTime(new Timestamp(System.currentTimeMillis())); // Set the current time as the start time
        userCourse.setStatus("upcoming"); // Set the status to "upcoming"
        userCourse.setRedoCount(0); // Initialize redo count to 0

        // Save the UserCourse entry
        userCourseRepository.save(userCourse);

        return "Course added to your upcoming list.";
    }


    // Fetch upcoming courses
    public List<UserCourse> getUpcomingCourses(Long userId) {
        return userCourseRepository.findByUserIdAndStatus(userId, "upcoming");
    }

    // Fetch favourites
    public List<UserCourse> getFavourites(Long userId) {
        return userCourseRepository.findByUserIdAndStatus(userId, "favourite");
    }

    // Toggle favourite status
    public String toggleFavourite(Long userId, Long courseId) {
        UserCourse userCourse = userCourseRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new RuntimeException("Course not found in user's list"));

        if (userCourse.getStatus().equals("favourite")) {
            userCourse.setStatus("upcoming"); // Remove from favourites
        } else {
            userCourse.setStatus("favourite"); // Add to favourites
        }

        userCourseRepository.save(userCourse);
        return "Favourite status updated.";
    }
}