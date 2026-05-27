package com.example.ibminnovate.controller;

import com.example.ibminnovate.dto.UserDTO;
import com.example.ibminnovate.model.User;
import com.example.ibminnovate.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leaderboard")
@CrossOrigin(origins = "http://localhost:3000") // Allow frontend requests
public class LeaderboardController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getLeaderboard() {
        List<User> users = userRepository.findAllByOrderByExpDesc();

        // Debug: Log each user to identify any bad data causing the 500 error
        users.forEach(user -> {
            System.out.println("USER: " + user.getUsername() + " | XP: " + user.getExp());
        });

        List<UserDTO> dtoList = users.stream()
                .map(user -> new UserDTO(user.getUsername(), user.getExp()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }
}