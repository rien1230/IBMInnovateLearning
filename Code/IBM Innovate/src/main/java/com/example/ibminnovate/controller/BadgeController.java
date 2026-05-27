package com.example.ibminnovate.controller;

import com.example.ibminnovate.model.Course;
import com.example.ibminnovate.model.Friends;
import com.example.ibminnovate.model.User;
import com.example.ibminnovate.repo.CourseRepository;
import com.example.ibminnovate.repo.UserRepository;
import com.example.ibminnovate.repo.FriendsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/api/users/{userId}")
public class BadgeController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private FriendsRepository friendsRepository;

    @GetMapping("/badges")
    public ResponseEntity<Map<String, Boolean>> getUserBadges(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return ResponseEntity.ok(user.getBadges());
    }

    @PostMapping("/check-badges")
    public ResponseEntity<List<String>> checkForNewBadges(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> requestBody) {

        Map<String, Boolean> currentBadges = (Map<String, Boolean>) requestBody.get("currentBadges");
        if (currentBadges == null) {
            currentBadges = new HashMap<>();
        }

        List<String> newBadges = new ArrayList<>();
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Check Course Completer badges
        List<Course> completedCourses = courseRepository.findCompletedCoursesByUserId(userId);
        checkThresholdBadges("courseCompleter", completedCourses.size(), currentBadges, newBadges);

        // Check Friend badges
        List<Friends> friends = friendsRepository.findByUserAndStatus(user, "accepted");
        checkThresholdBadges("friend", friends.size(), currentBadges, newBadges);

        // Check Multitasker badges
        List<Course> ongoingCourses = courseRepository.findOngoingCoursesByUserId(userId);
        checkThresholdBadges("multitasker", ongoingCourses.size(), currentBadges, newBadges);

        // Check Weekend Warrior badge (special case)
        if (!currentBadges.getOrDefault("weekendWarrior-1", false)) {
            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                newBadges.add("weekendWarrior-1");
            }
        }

        // Save new badges to user if any
        if (!newBadges.isEmpty()) {
            Map<String, Boolean> userBadges = user.getBadges();
            for (String badgeId : newBadges) {
                userBadges.put(badgeId, true);
            }
            userRepository.save(user);
        }

        return ResponseEntity.ok(newBadges);
    }

    private void checkThresholdBadges(String badgeType, int currentValue,
                                      Map<String, Boolean> currentBadges,
                                      List<String> newBadges) {
        // Define thresholds for each badge type
        Map<String, List<Integer>> badgeThresholds = Map.of(
                "courseCompleter", List.of(1, 3, 5),
                "friend", List.of(1, 5),
                "multitasker", List.of(2, 3, 5)
        );

        List<Integer> thresholds = badgeThresholds.get(badgeType);
        if (thresholds == null) return;

        for (int i = 0; i < thresholds.size(); i++) {
            int threshold = thresholds.get(i);
            String badgeId = badgeType + "-" + (i + 1); // badgeId like "courseCompleter-1"

            if (currentValue >= threshold && !currentBadges.getOrDefault(badgeId, false)) {
                newBadges.add(badgeId);
            }
        }
    }
}
