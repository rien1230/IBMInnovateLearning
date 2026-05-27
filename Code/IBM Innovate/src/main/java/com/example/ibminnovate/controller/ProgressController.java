package com.example.ibminnovate.controller;

import com.example.ibminnovate.dto.CompleteCourseRequest;
import com.example.ibminnovate.dto.OngoingCourseRequest;
import com.example.ibminnovate.dto.StartCourseRequest;
import com.example.ibminnovate.dto.UserCourseDTO;
import com.example.ibminnovate.exception.ResourceNotFoundException;
import com.example.ibminnovate.model.Course;
import com.example.ibminnovate.model.CourseHistory;
import com.example.ibminnovate.model.User;
import com.example.ibminnovate.model.UserCourse;
import com.example.ibminnovate.repo.CourseRepository;
import com.example.ibminnovate.repo.UserCourseRepository;
import com.example.ibminnovate.repo.UserRepository;
import com.example.ibminnovate.service.ProgressService;
import com.example.ibminnovate.service.UserCourseService;
import com.example.ibminnovate.service.UserStreakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/progress")
public class ProgressController {

    @Autowired
    private ProgressService progressService;

    @Autowired
    private UserCourseService userCourseService;

    @Autowired
    private UserStreakService userStreakService;

    @Autowired
    private UserCourseRepository userCourseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping("/ongoing-courses/{userId}")
    public ResponseEntity<List<UserCourseDTO>> getOngoingCourses(@PathVariable Long userId) {
        List<UserCourseDTO> ongoingCourses = progressService.getOngoingCourses(userId);
        System.out.println("Ongoing Courses for User " + userId + ": " + ongoingCourses);
        return ResponseEntity.ok(ongoingCourses);
    }

    @GetMapping("/completed-courses/{userId}")
    public ResponseEntity<List<UserCourseDTO>> getCompletedCourses(@PathVariable Long userId) {
        List<UserCourseDTO> completedCourses = progressService.getCompletedCourses(userId);
        System.out.println("Completed Courses for User " + userId + ": " + completedCourses);
        return ResponseEntity.ok(completedCourses);
    }

    @GetMapping("/achievements/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getAchievements(@PathVariable Long userId) {
        List<Map<String, Object>> achievements = progressService.getAchievements(userId);
        System.out.println("Achievements for User " + userId + ": " + achievements);
        return ResponseEntity.ok(achievements);
    }

    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startCourse(@RequestBody StartCourseRequest request) {
        System.out.println("Received start request: " + request);
        try {
            progressService.updateStartTime(request.getUserId(), request.getCourseId(), Timestamp.from(Instant.now()));
            userStreakService.updateStreak(request.getUserId()); //Update streak
            return ResponseEntity.ok(Map.of("message", "Start time recorded!"));
        } catch (Exception e) {
            System.err.println("Error in startCourse: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/complete-course")
    public ResponseEntity<Map<String, String>> markCourseAsCompleted(@RequestBody CompleteCourseRequest request) {
        try {
            // Fetch the user and course
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Course course = courseRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Course not found"));

            // Calculate time taken to complete the course
            long endTime = System.currentTimeMillis(); // Current time as end time
            long timeTakenMillis = endTime - request.getStartTime();

            // Convert course duration to milliseconds
            long courseDurationMillis = parseDurationToMillis(course.getDuration());

            // Award EXP
            int expAwarded = 15; // Base EXP
            if (timeTakenMillis < courseDurationMillis) {
                expAwarded += 25; // Bonus EXP for completing faster
            }

            // Update user's EXP and level
            user.setExp(user.getExp() + expAwarded);
            userRepository.save(user);

            // Mark the course as completed
            progressService.markCourseAsCompleted(request.getUserId(), request.getCourseId());

            // Update the user's streak
            userStreakService.updateStreak(request.getUserId());

            return ResponseEntity.ok(Map.of(
                    "message", "Course marked as completed!",
                    "expAwarded", String.valueOf(expAwarded)
            ));
        } catch (Exception e) {
            System.err.println("Error in markCourseAsCompleted: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to complete course: " + e.getMessage()));
        }
    }

    // Helper method to parse course duration into milliseconds
    private long parseDurationToMillis(String duration) {
        if (duration.contains("minutes")) {
            int minutes = Integer.parseInt(duration.replaceAll("[^0-9]", ""));
            return minutes * 60 * 1000L; // Convert minutes to milliseconds
        } else if (duration.contains("hours")) {
            int hours = Integer.parseInt(duration.replaceAll("[^0-9]", ""));
            return hours * 60 * 60 * 1000L; // Convert hours to milliseconds
        } else if (duration.contains("+")) {
            // Handle "10+ hours" or similar cases
            int hours = Integer.parseInt(duration.replaceAll("[^0-9]", ""));
            return hours * 60 * 60 * 1000L; // Convert hours to milliseconds
        } else {
            throw new IllegalArgumentException("Invalid duration format: " + duration);
        }
    }

    @PostMapping("/pause-course")
    public ResponseEntity<Map<String, String>> pauseCourse(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long courseId = request.get("courseId");

        UserCourse userCourse = userCourseRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("UserCourse not found"));

        userCourse.setPauseTime(new Timestamp(System.currentTimeMillis())); // Set pause time
        userCourse.setStatus("paused"); // Update status to "paused"
        userCourseRepository.save(userCourse);

        return ResponseEntity.ok(Map.of("message", "Course paused successfully"));
    }

    // Endpoint to resume a course
    @PostMapping("/resume-course")
    public ResponseEntity<Map<String, String>> resumeCourse(@RequestBody Map<String, Long> request) {
        Long userId = request.get("userId");
        Long courseId = request.get("courseId");

        UserCourse userCourse = userCourseRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ResourceNotFoundException("UserCourse not found"));

        userCourse.setPauseTime(null); // Clear pause time
        userCourse.setStatus("ongoing"); // Update status to "ongoing"
        userCourseRepository.save(userCourse);
        userStreakService.updateStreak(userId); //Update streak

        return ResponseEntity.ok(Map.of("message", "Course resumed successfully"));
    }

    @PostMapping("/progress/ongoing-courses/{userId}")
    public ResponseEntity<Map<String, String>> addOngoingCourse(@PathVariable Long userId, @RequestBody OngoingCourseRequest request) {
        progressService.addOngoingCourse(userId, request.getCourseId(), request.getStatus());
        return ResponseEntity.ok(Map.of("message", "Course added to ongoing courses"));
    }

    @GetMapping("/course-status/{userId}/{courseId}")
    public ResponseEntity<Map<String, String>> getCourseStatus(@PathVariable Long userId, @PathVariable Long courseId) {
        String status = progressService.getCourseStatus(userId, courseId);
        return ResponseEntity.ok(Map.of("status", status));
    }

    @GetMapping("/course-history/{userId}/{courseId}")
    public ResponseEntity<List<CourseHistory>> getCourseHistory(@PathVariable Long userId, @PathVariable Long courseId) {
        List<CourseHistory> history = progressService.getCourseHistory(userId, courseId);
        return ResponseEntity.ok(history);
    }

    //Retrieve user's current streak to display
    @GetMapping("/streak/{userId}")
    public ResponseEntity<Map<String, Integer>> getStreak(@PathVariable Long userId) {
        int streak = userStreakService.getCurrentStreak(userId);
        return ResponseEntity.ok(Map.of("streak", streak));
    }

    @PostMapping("/add-to-upcoming")
    public ResponseEntity<?> addToUpcoming(@RequestBody Map<String, String> request) {
        Long userId = Long.parseLong(request.get("userId"));
        Long courseId = Long.parseLong(request.get("courseId"));

        try {
            String message = userCourseService.addToUpcoming(userId, courseId);
            return ResponseEntity.ok(Collections.singletonMap("message", message));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/upcoming-courses/{userId}")
    public ResponseEntity<?> getUpcomingCourses(@PathVariable Long userId) {
        try {
            List<UserCourse> upcomingCourses = userCourseService.getUpcomingCourses(userId);
            return ResponseEntity.ok(upcomingCourses);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    @GetMapping("/favourites/{userId}")
    public ResponseEntity<?> getFavourites(@PathVariable Long userId) {
        try {
            List<UserCourse> favourites = userCourseService.getFavourites(userId);
            return ResponseEntity.ok(favourites);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }
    @PostMapping("/toggle-favourite")
    public ResponseEntity<?> toggleFavourite(@RequestBody Map<String, String> request) {
        Long userId = Long.parseLong(request.get("userId"));
        Long courseId = Long.parseLong(request.get("courseId"));

        try {
            String message = userCourseService.toggleFavourite(userId, courseId);
            return ResponseEntity.ok(Collections.singletonMap("message", message));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @GetMapping("/completed-count")
    public ResponseEntity<Integer> getCompletedCoursesCount(@RequestParam Long userId) {
        int count = progressService.getCompletedCoursesCount(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/ongoing-count")
    public ResponseEntity<Integer> getOngoingCoursesCount(@RequestParam Long userId) {
        int count = progressService.getOngoingCoursesCount(userId);
        return ResponseEntity.ok(count);
    }
}