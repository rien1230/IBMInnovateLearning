package com.example.ibminnovate.controller;

import com.example.ibminnovate.model.User;
import com.example.ibminnovate.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class AvatarController {

    @Autowired
    private UserRepository userRepository;

    //endpoint for saving a user's unique url for their avatar
    @PostMapping("/{userId}/avatar")
    public ResponseEntity<String> saveAvatarUrl(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        String avatarUrl = request.get("avatarUrl");
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("Avatar URL is required");
        }

        //checking user exists before saving their avatarurl
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);

        return ResponseEntity.ok("Avatar URL saved successfully!");
    }

    //endpoint to retrieve the user's avatarurl
    @GetMapping("/{userId}/avatar")
    public ResponseEntity<Map<String, String>> getAvatarUrl(@PathVariable Long userId) {
        //error handling
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getAvatarUrl() == null) {
            return ResponseEntity.notFound().build();
        }

        //preparing the response of their avatar after the url has been saved and retrieved
        Map<String, String> response = new HashMap<>();
        response.put("avatarUrl", user.getAvatarUrl());
        return ResponseEntity.ok(response);
    }
}
