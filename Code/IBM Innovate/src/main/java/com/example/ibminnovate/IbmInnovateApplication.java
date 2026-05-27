package com.example.ibminnovate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import com.example.ibminnovate.model.Course;
import com.example.ibminnovate.model.Friends;
import com.example.ibminnovate.model.User;
import com.example.ibminnovate.repo.CourseRepository;
import com.example.ibminnovate.repo.FriendsRepository;
import com.example.ibminnovate.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class IbmInnovateApplication {

    public static void main(String[] args) {
        SpringApplication.run(IbmInnovateApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(CourseRepository courseRepository, UserRepository userRepository, FriendsRepository friendsRepository) {
        return args -> {
            // Check if the database is already populated
            if (courseRepository.count() == 0) {
                // Create and save courses
                List<Course> courses = Arrays.asList(
                        new Course("Introduction to Large Language Models", Arrays.asList("AI"), "90 minutes", Arrays.asList("English")),
                        new Course("Build Your First Chatbot", Arrays.asList("AI"), "60 minutes", Arrays.asList("English")),
                        new Course("Classifying Data Using IBM Granite", Arrays.asList("AI"), "60 minutes", Arrays.asList("English")),
                        new Course("Explore Text to Speech Using IBM watsonx", Arrays.asList("AI"), "60 minutes", Arrays.asList("English")),
                        new Course("IBM Granite Models for Software Development", Arrays.asList("AI"), "60 minutes", Arrays.asList("English")),
                        new Course("Summarizing Data Using IBM Granite", Arrays.asList("AI"), "60 minutes", Arrays.asList("English")),
                        new Course("Use Generative AI for Software Development", Arrays.asList("AI"), "60 minutes", Arrays.asList("English")),
                        new Course("Getting Started with Artificial Intelligence", Arrays.asList("AI"), "3 hours", Arrays.asList("English")),
                        new Course("Artificial Intelligence Fundamentals", Arrays.asList("AI"), "10+ hours", Arrays.asList("English", "Portuguese", "Chinese", "French", "Hindi", "Spanish", "Japanese")),
                        new Course("Building AI Solutions Using Advanced Algorithms and Open Source Frameworks", Arrays.asList("AI"), "20+ hours", Arrays.asList("English")),
                        new Course("Building Trustworthy AI Enterprise Solutions", Arrays.asList("AI"), "10+ hours", Arrays.asList("English")),
                        new Course("Code Generation and Optimization Using IBM Granite", Arrays.asList("AI"), "210 minutes", Arrays.asList("English")),
                        new Course("Data Classification and Summarization Using IBM Granite", Arrays.asList("AI"), "180-240 minutes", Arrays.asList("English")),
                        new Course("Generative AI in Action", Arrays.asList("AI"), "5+ hours", Arrays.asList("English")),
                        new Course("Getting Started with Cybersecurity", Arrays.asList("Cybersecurity"), "3 hours", Arrays.asList("English")),
                        new Course("Cybersecurity Fundamentals", Arrays.asList("Cybersecurity"), "7.5 hours", Arrays.asList("English")),
                        new Course("Enterprise Security in Practice", Arrays.asList("Cybersecurity"), "10+ hours", Arrays.asList("English")),
                        new Course("Getting Started with Threat Intelligence and Hunting", Arrays.asList("Cybersecurity"), "5 hours", Arrays.asList("English")),
                        new Course("Security Operations Center in Practice", Arrays.asList("Cybersecurity"), "20+ hours", Arrays.asList("English", "Spanish", "Portuguese")),
                        new Course("Getting Started with Data", Arrays.asList("Data Science"), "3 hours", Arrays.asList("English")),
                        new Course("Data Fundamentals", Arrays.asList("Data Science"), "7 hours", Arrays.asList("English", "French")),
                        new Course("Enterprise Data Science in Practice", Arrays.asList("Data Science"), "10+ hours", Arrays.asList("English")),
                        new Course("Machine Learning for Data Science Projects", Arrays.asList("Data Science"), "20+ hours", Arrays.asList("English", "Spanish", "Portuguese")),
                        new Course("Cloud Computing Fundamentals", Arrays.asList("Cloud"), "10+ hours", Arrays.asList("English")),
                        new Course("Journey to Cloud: Envisioning Your Solution", Arrays.asList("Cloud"), "5 hours", Arrays.asList("English"))
                );

                // Save all courses to the database
                courseRepository.saveAll(courses);
            }
            if (userRepository.count() == 0) {
                // Create users with encoded passwords
                PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

                User sophie = new User("sophie_williams", "sophie.williams@gmail.com",
                        encoder.encode("password1"), 120);
                User daniel = new User("daniel.tan", "daniel.tan@gmail.com",
                        encoder.encode("password1"), 5);
                User luis = new User("luis.garcia", "luis.garcia@gmail.com",
                        encoder.encode("password1"), 12);
                User emily = new User("emily_zhang", "emily.zhang@gmail.com",
                        encoder.encode("password1"), 12);
                userRepository.saveAll(List.of(sophie, daniel, luis, emily));

                // Add friends
                Friends accepted = new Friends();
                accepted.setUser(sophie);
                accepted.setFriendUsername("daniel.tan");
                accepted.setStatus("ACCEPTED");

                Friends pending1 = new Friends();
                pending1.setUser(luis);
                pending1.setFriendUsername("sophie_williams");
                pending1.setStatus("PENDING");

                Friends pending2 = new Friends();
                pending2.setUser(emily);
                pending2.setFriendUsername("daniel.tan");
                pending2.setStatus("PENDING");

                friendsRepository.saveAll(List.of(accepted, pending1, pending2));
            }
        };
    }
}