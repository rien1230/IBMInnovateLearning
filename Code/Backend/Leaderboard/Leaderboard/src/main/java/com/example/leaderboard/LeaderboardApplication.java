package com.example.leaderboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EnableJpaRepositories("com.example.leaderboard.repository")
@EntityScan("com.example.leaderboard.models")
public class LeaderboardApplication {
	public static void main(String[] args) {
		SpringApplication.run(LeaderboardApplication.class, args);
	}
}
