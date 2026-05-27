package com.example.ibminnovate.controller;

import com.example.ibminnovate.dto.ExpRequest;
import com.example.ibminnovate.dto.ExpResponse;
import com.example.ibminnovate.model.User;
import com.example.ibminnovate.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api")
public class ExpController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/get-exp")
    public ResponseEntity<ExpResponse> getExp(@RequestParam Long userId) {
        try {
            // Fetch the user from the database
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Return the user's EXP and level
            ExpResponse response = new ExpResponse();
            response.setExp(user.getExp());
            response.setLevel(user.getLevel());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error in getExp: " + e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

    @Transactional
    @PostMapping("/save-exp")
    public ResponseEntity<String> saveExp(@RequestBody ExpRequest request) {
        try {
            // Validate request
            if (request.getExp() < 0 || request.getLevel() < 1) {
                return ResponseEntity.badRequest().body("Invalid EXP or level value");
            }

            // Fetch the user from the database
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Update the user's EXP and level
            user.setExp(request.getExp()); // Set the total EXP
            user.setLevel(request.getLevel());

            // Save the updated user
            userRepository.save(user);

            return ResponseEntity.ok("EXP saved successfully");
        } catch (Exception e) {
            System.err.println("Error in saveExp: " + e.getMessage());
            return ResponseEntity.status(500).body("Failed to save EXP: " + e.getMessage());
        }
    }
}