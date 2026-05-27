package com.example.ibminnovate.service;

import com.example.ibminnovate.dto.UserCourseDTO;
import com.example.ibminnovate.model.*;
import com.example.ibminnovate.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ProgressService {

    @Autowired
    private UserCourseRepository userCourseRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseHistoryRepository courseHistoryRepository;


    @Autowired
    private ProgressRepository progressRepository;

    public List<UserCourseDTO> getOngoingCourses(Long userId) {
        List<UserCourse> userCourses = userCourseRepository.findByUserIdAndStatusIn(userId, List.of("ongoing", "paused"));
        return userCourses.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<UserCourseDTO> getCompletedCourses(Long userId) {
        List<UserCourse> userCourses = userCourseRepository.findCompletedCoursesByUserId(userId);
        return userCourses.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private UserCourseDTO convertToDTO(UserCourse userCourse) {
        UserCourseDTO dto = new UserCourseDTO();
        dto.setCourseId(userCourse.getCourse().getId());
        dto.setCourseName(userCourse.getCourse().getCourseName());
        dto.setStartTime(userCourse.getStartTime());
        dto.setEndTime(userCourse.getEndTime());
        dto.setDuration(formatDuration(userCourse.getStartTime(), userCourse.getEndTime()));
        dto.setLanguages(userCourse.getCourse().getLanguages());
        dto.setRedoCount(userCourse.getRedoCount());
        return dto;
    }

    public List<Map<String, Object>> getAchievements(Long userId) {
        // Example: Fetch achievements from the database or generate them
        List<Map<String, Object>> achievements = new ArrayList<>();

        Map<String, Object> achievement1 = new HashMap<>();
        achievement1.put("id", 1);
        achievement1.put("title", "First Course Completed");
        achievement1.put("description", "You completed your first course!");
        achievements.add(achievement1);

        Map<String, Object> achievement2 = new HashMap<>();
        achievement2.put("id", 2);
        achievement2.put("title", "5 Courses Completed");
        achievement2.put("description", "You completed 5 courses!");
        achievements.add(achievement2);

        return achievements;
    }

    private String formatDuration(Timestamp startTime, Timestamp endTime) {
        if (startTime == null) return "N/A";
        long durationInMillis = (endTime != null ? endTime.getTime() : System.currentTimeMillis()) - startTime.getTime();
        long days = durationInMillis / (1000 * 60 * 60 * 24);
        long hours = (durationInMillis % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (durationInMillis % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (durationInMillis % (1000 * 60)) / 1000;
        return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
    }

    @Transactional
    public void updateStartTime(Long userId, Long courseId, Timestamp startTime) {
        if (userId == null || courseId == null || startTime == null) {
            throw new IllegalArgumentException("userId, courseId, and startTime must not be null");
        }

        try {
            UserCourseId userCourseId = new UserCourseId(userId, courseId);
            UserCourse userCourse = userCourseRepository.findById(userCourseId)
                    .orElseGet(() -> {
                        UserCourse newUserCourse = new UserCourse();
                        newUserCourse.setId(userCourseId);
                        User user = userRepository.findByID(userId)
                                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
                        Course course = courseRepository.findById(courseId)
                                .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
                        newUserCourse.setUser(user);
                        newUserCourse.setCourse(course);
                        newUserCourse.setStatus("ongoing");
                        newUserCourse.setRedoCount(0);
                        return newUserCourse;
                    });

            if ("completed".equals(userCourse.getStatus())) {
                userCourse.setRedoCount(userCourse.getRedoCount() + 1);
            }

            userCourse.setStartTime(startTime);
            userCourse.setStatus("ongoing");
            userCourseRepository.save(userCourse);
        } catch (RuntimeException e) {
            System.err.println("Error in updateStartTime: " + e.getMessage());
            throw e; // Re-throw the exception to trigger a rollback
        }
    }

    @Transactional
    public void markCourseAsCompleted(Long userId, Long courseId) {
        UserCourseId userCourseId = new UserCourseId(userId, courseId);
        UserCourse userCourse = userCourseRepository.findById(userCourseId)
                .orElseThrow(() -> new RuntimeException("UserCourse not found"));

        // Save the current attempt to CourseHistory
        CourseHistory history = new CourseHistory();
        history.setUserId(userId);
        history.setCourseId(courseId);
        history.setStartTime(userCourse.getStartTime());
        history.setEndTime(new Timestamp(System.currentTimeMillis()));

        // Calculate the redoCount for the history entry
        int redoCount = userCourse.getRedoCount(); // Current redoCount
        history.setRedoCount(redoCount); // Set the redoCount for the history entry

        history.setDuration(calculateDuration(userCourse.getStartTime(), new Timestamp(System.currentTimeMillis())));
        courseHistoryRepository.save(history);

        // Update the UserCourse entity
        if ("completed".equals(userCourse.getStatus())) {
            // If the course was already completed, increment the redoCount
            userCourse.setRedoCount(redoCount + 1);
        } else {
            // If it's the first completion, set the status to "completed"
            userCourse.setStatus("completed");
        }

        userCourse.setEndTime(new Timestamp(System.currentTimeMillis())); // Set end time
        userCourseRepository.save(userCourse);
    }

    private String calculateDuration(Timestamp startTime, Timestamp endTime) {
        long durationInMillis = endTime.getTime() - startTime.getTime();
        long hours = TimeUnit.MILLISECONDS.toHours(durationInMillis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(durationInMillis) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(durationInMillis) % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Transactional
    public void addOngoingCourse(Long userId, Long courseId, String status) {
        UserCourseId userCourseId = new UserCourseId(userId, courseId);
        UserCourse userCourse = userCourseRepository.findById(userCourseId)
                .orElseGet(() -> {
                    UserCourse newUserCourse = new UserCourse();
                    newUserCourse.setId(userCourseId);
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    Course course = courseRepository.findById(courseId)
                            .orElseThrow(() -> new RuntimeException("Course not found"));
                    newUserCourse.setUser(user);
                    newUserCourse.setCourse(course);
                    newUserCourse.setStatus(status);
                    newUserCourse.setRedoCount(0);
                    return newUserCourse;
                });
        userCourse.setStatus(status);
        userCourseRepository.save(userCourse);
    }
    public String getCourseStatus(Long userId, Long courseId) {
        // Fetch the UserCourse entity for the given user and course
        Optional<UserCourse> userCourse = userCourseRepository.findByUserIdAndCourseId(userId, courseId);

        if (userCourse.isEmpty()) {
            // If no record exists, the course has not been started
            return "not started";
        }

        // Check the status of the course
        if (userCourse.get().getEndTime() != null) {
            // If endTime is set, the course is completed
            return "completed";
        } else if (userCourse.get().getStartTime() != null) {
            // If startTime is set but endTime is null, the course is ongoing
            return "ongoing";
        } else {
            // If neither startTime nor endTime is set, the course is not started
            return "not started";
        }
    }
    public List<CourseHistory> getCourseHistory(Long userId, Long courseId) {
        return courseHistoryRepository.findByUserIdAndCourseId(userId, courseId);
    }

    public int getCompletedCoursesCount(Long userId) {
        return progressRepository.countByUserIDAndStatus(userId, "completed");
    }

    public int getOngoingCoursesCount(Long userId) {
        return progressRepository.countByUserIDAndStatusIn(userId, List.of("ongoing", "paused"));
    }
}