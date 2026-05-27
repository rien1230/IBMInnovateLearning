package com.example.ibminnovate.controller;

import com.example.ibminnovate.model.Review;
import com.example.ibminnovate.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
// maps to the reviews.json file
// ReviewController is responsible for fetching stored reviews, saving new reviews,
// adding reviews to the json file and reading reviews from the json file
@RequestMapping("/api/reviews")
public class ReviewController {

    private static final String FILE_PATH = "reviews.json";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserService userService; // Declare the UserService

    // Inject UserService through constructor
    @Autowired
    public ReviewController(UserService userService) {
        this.userService = userService;
    }

    // Fetch stored reviews
    @GetMapping
    public ResponseEntity<List<Review>> getReviews() {
        List<Review> reviews = readReviewsFromFile();
        return ResponseEntity.ok(reviews);
    }

    // Save new review
    @PostMapping
    public ResponseEntity<String> addReview(@RequestBody Review newReview) {
        List<Review> reviews = readReviewsFromFile();
        reviews.add(newReview);
        writeReviewsToFile(reviews);
        return ResponseEntity.ok("Review added successfully");
    }

    // Fetch the current authenticated user's username using userService
    @GetMapping("/current-username")
    public ResponseEntity<String> getCurrentUsername() {
        // Use userService to fetch the username
        String username = userService.getCurrentUsername();
        return ResponseEntity.ok(username); // Return the username
    }

    // Read reviews from file
    private List<Review> readReviewsFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();

        try {
            return objectMapper.readValue(file, new TypeReference<List<Review>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Write reviews to file
    private void writeReviewsToFile(List<Review> reviews) {
        try {
            objectMapper.writeValue(new File(FILE_PATH), reviews);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
