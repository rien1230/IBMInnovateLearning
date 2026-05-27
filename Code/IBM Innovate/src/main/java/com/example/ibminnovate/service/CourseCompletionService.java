package com.example.ibminnovate.service;

import com.example.ibminnovate.model.Progress;
import com.example.ibminnovate.model.Achievement;
import com.example.ibminnovate.model.User;
import com.example.ibminnovate.repo.ProgressRepository;
import com.example.ibminnovate.repo.AchievementRepository;
import com.example.ibminnovate.repo.UserRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
public class CourseCompletionService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProgressRepository progressRepository;

    @Autowired
    private AchievementRepository achievementRepository;

    // Method to fetch completed courses from IBM Skills
    public void fetchCompletedCourses(User user) {
        String ibmSkillsUrl = "https://skills.yourlearning.ibm.com/learning/completed";
        String sessionCookie = "YOUR_SESSION_COOKIE"; // Replace with actual session cookie after authentication

        try {
            // Fetch the completed courses page
            Document document = Jsoup.connect(ibmSkillsUrl)
                    .cookie("SESSION", sessionCookie)
                    .get();

            // Extract completed courses
            Elements courseElements = document.select(".completed-course"); // Adjust selector based on the page structure
            for (Element courseElement : courseElements) {
                String courseName = courseElement.select(".course-name").text(); // Adjust selector
                String courseUrl = courseElement.select("a").attr("href"); // Adjust selector

                // Check if the course is already recorded
                Progress existingProgress = progressRepository.findByUserAndCourseName(user, courseName);
                if (existingProgress == null) {

                    // Unlock achievement
                    Achievement achievement = new Achievement();
                    achievement.setTitle("Completed: " + courseName);
                    achievement.setDescription("You completed the course: " + courseName);
                    achievement.setDateEarned(LocalDate.now());
                    achievementRepository.save(achievement);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Scheduled task to check for completed courses daily
    @Scheduled(cron = "0 0 0 * * ?") // Runs at midnight every day
    public void checkForCompletedCourses() {
        List<User> users =  userRepository.findAll();// Fetch all users from the database
        for (User user : users) {
            fetchCompletedCourses(user);
        }
    }
}