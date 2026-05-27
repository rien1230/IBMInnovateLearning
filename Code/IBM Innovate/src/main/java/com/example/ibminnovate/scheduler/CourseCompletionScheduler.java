package com.example.ibminnovate.scheduler;

import com.example.ibminnovate.model.User;
import com.example.ibminnovate.repo.UserRepository;
import com.example.ibminnovate.service.CourseCompletionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CourseCompletionScheduler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseCompletionService courseCompletionService;

    // This method runs every day at midnight
    @Scheduled(cron = "0 0 0 * * ?") // Cron expression for "daily at midnight"
    public void checkForCompletedCourses() {
        System.out.println("Checking for completed courses...");
        // Fetch all users and check their completed courses
        List<User> users = userRepository.findAll();
        for (User user : users) {
            courseCompletionService.fetchCompletedCourses(user);
        }
    }
}
