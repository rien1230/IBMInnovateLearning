package com.example.ibminnovate.dto;

import com.example.ibminnovate.model.UserLeaderboard;
import com.example.ibminnovate.repo.UserLeaderboardRepository;
import com.example.ibminnovate.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LeaderboardDataInitializer {

    @Bean
    CommandLineRunner initLeaderboard(UserRepository userRepo,
                                      UserLeaderboardRepository leaderboardRepo) {
        return args -> {
            // Clear existing data (since we're using create-drop)
            leaderboardRepo.deleteAll();

            // Get all users with exp > 0 from main user table
            userRepo.findAll().stream()
                    .filter(u -> u.getExp() > 0)
                    .forEach(user -> {
                        leaderboardRepo.save(new UserLeaderboard(
                                user.getUsername(),
                                user.getExp()
                        ));
                    });

            System.out.println("Initialized leaderboard data");
        };
    }
}
