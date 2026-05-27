package com.example.ibminnovate.controller;

import com.example.ibminnovate.model.User;
import com.example.ibminnovate.repo.UserRepository;
import com.example.ibminnovate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class ProfileController {

    private static final String UPLOAD_DIR = "uploads/";

    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserRepository userRepository;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @GetMapping("/update")
    public String showProfileUpdateForm() {
        return "profile-management"; // Thymeleaf template for the update form
    }

    // Update Name
    @PostMapping("/update-name")
    public ResponseEntity<String> updateName(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString()); // Extract userId
            String name = (String) request.get("name"); // Extract name
            userService.updateName(userId, name);
            return ResponseEntity.ok("Name updated successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/update-email")
    public ResponseEntity<String> updateEmail(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString()); // Extract userId
            String email = (String) request.get("email"); // Extract email
            userService.updateEmail(userId, email);
            return ResponseEntity.ok("Email updated successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/update-password")
    public ResponseEntity<String> updatePassword(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString()); // Extract userId
            String currentPassword = (String) request.get("currentPassword"); // Extract currentPassword
            String newPassword = (String) request.get("newPassword"); // Extract newPassword
            userService.updatePassword(userId, currentPassword, newPassword);
            return ResponseEntity.ok("Password updated successfully!");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/avatar")
    public ResponseEntity<Map<String, String>> getAvatar() {
        try {
            // Fetch the current user's avatar URL
            String username = userService.getCurrentUsername();
            String avatarUrl = userService.getAvatarUrl(username);

            if (avatarUrl != null) {
                return ResponseEntity.ok(Collections.singletonMap("avatarUrl", avatarUrl));
            } else {
                // Return a default avatar URL if no avatar is found
                String defaultAvatarUrl = "http://localhost:8080/images/default-avatar.png";
                return ResponseEntity.ok(Collections.singletonMap("avatarUrl", defaultAvatarUrl));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to fetch avatar URL"));
        }
    }



    @PostMapping("/upload-profile-image")
    public ResponseEntity<Map<String, String>> uploadProfileImage(@RequestParam("image") MultipartFile file) {
        try {
            // Validate file type
            if (!file.getContentType().equals("image/png")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Collections.singletonMap("error", "Only PNG files are allowed."));
            }

            // Ensure the upload directory exists
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate a unique filename for the uploaded file
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);

            // Save the file to the upload directory
            Files.copy(file.getInputStream(), filePath);

            // Save the file path in the database
            String username = userService.getCurrentUsername(); // Replace with the actual username (if needed)
            userService.updateProfilePicture(username, fileName);

            // Return the URL of the uploaded file
            String fileUrl = "http://localhost:8080/images/" + fileName;
            return ResponseEntity.ok(Collections.singletonMap("imageUrl", fileUrl));
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to upload image"));
        }
    }

    @GetMapping("/images/{fileName}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileName) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_PNG) // Adjust based on the image type
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/get-profile-image")
    public ResponseEntity<Map<String, String>> getProfileImage() {
        try {
            // Fetch the profile picture from the database
            String username = userService.getCurrentUsername();
            String profilePicture = userService.getProfilePicture(username);

            if (profilePicture != null) {
                String imageUrl = "http://localhost:8080/images/" + profilePicture;
                return ResponseEntity.ok(Collections.singletonMap("imageUrl", imageUrl));
            } else {
                // Return a default image URL if no profile picture is found
                String defaultImageUrl = "http://localhost:8080/images/default.png";
                return ResponseEntity.ok(Collections.singletonMap("imageUrl", defaultImageUrl));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to fetch profile image"));
        }
    }
    @GetMapping("/get-profile-details")
    public ResponseEntity<Map<String, String>> getProfileDetails() {
        try {
            String username = userService.getCurrentUsername();
            String email = userService.getEmail(username);
            String profilePicture = userService.getProfilePicture(username);
            String avatarUrl = userService.getAvatarUrl(username);

            return ResponseEntity.ok(Map.of(
                    "username", username,
                    "email", email,
                    "imageUrl", profilePicture != null ? "http://localhost:8080/images/" + profilePicture : "http://localhost:8080/images/default.png",
                    "avatarUrl", avatarUrl != null ? avatarUrl : "http://localhost:8080/images/default-avatar.png"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch profile details"));
        }
    }

    @PostMapping("/{userId}/avatar")
    public ResponseEntity<String> saveAvatar(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        String avatarUrl = request.get("avatarUrl");
        if (avatarUrl == null || avatarUrl.isEmpty()) {
            return ResponseEntity.badRequest().body("Avatar URL is required");
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);

        return ResponseEntity.ok("Avatar saved successfully!");
    }

    // Fetch Avatar URL
    @GetMapping("/{userId}/avatar")
    public ResponseEntity<Map<String, String>> getAvatar(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null || user.getAvatarUrl() == null) {
            return ResponseEntity.notFound().build();
        }

        Map<String, String> response = new HashMap<>();
        response.put("avatarUrl", user.getAvatarUrl());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<Map<String, Object>> getProfile(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Return user details, including userId
        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getID());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("avatarUrl", user.getAvatarUrl());
        return ResponseEntity.ok(response);
    }
}