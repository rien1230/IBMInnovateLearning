package com.example.ibminnovate.controller;

import com.example.ibminnovate.model.User;
import com.example.ibminnovate.repo.UserRepository;
import com.example.ibminnovate.utility.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/oauth2/callback")
public class OAuth2CallbackController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil; // Inject JwtUtil

    @GetMapping("/google")
    public ResponseEntity<Map<String, Object>> handleGoogleCallback(
            @RequestParam String code, // Authorization code from Google
            HttpServletResponse response) {

        // Exchange the authorization code for an access token
        String accessToken = exchangeCodeForAccessToken(code);

        // Fetch user details from Google using the access token
        Map<String, String> userDetails = fetchUserDetailsFromGoogle(accessToken);

        // Extract user details
        String email = userDetails.get("email");
        String name = userDetails.get("name");

        // Check if the user already exists
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            // User already exists
            user = userOptional.get();
        } else {
            // Create a new Google user
            user = new User();
            user.setEmail(email);
            user.setUsername(name);
            user.setUserType("google"); // Set userType to "google"
            userRepository.save(user);
        }

        // Generate a JWT token for the user
        String token = jwtUtil.generateToken(user.getEmail());

        // Create a response map with user details and token
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("userId", user.getID());
        responseMap.put("email", user.getEmail());
        responseMap.put("username", user.getUsername());
        responseMap.put("userType", user.getUserType());
        responseMap.put("token", token); // Include the token in the response

        // Log the response for debugging
        System.out.println("Google OAuth response: " + responseMap);

        // Return the response as JSON
        return ResponseEntity.ok(responseMap);
    }

    private String exchangeCodeForAccessToken(String code) {
        try {
            // Google OAuth2 token endpoint
            String tokenUrl = "https://oauth2.googleapis.com/token";

            // Prepare the request body
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("code", code);
            requestBody.put("client_id", "YOUR_GOOGLE_CLIENT_ID");
            requestBody.put("client_secret", "YOUR_GOOGLE_CLIENT_SECRET");
            requestBody.put("redirect_uri", "YOUR_REDIRECT_URI");
            requestBody.put("grant_type", "authorization_code");

            // Send the request to Google's token endpoint
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.postForObject(tokenUrl, requestBody, String.class);

            // Parse the response to extract the access token
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> responseMap = mapper.readValue(response, Map.class);
            return responseMap.get("access_token");
        } catch (Exception e) {
            throw new RuntimeException("Failed to exchange code for access token", e);
        }
    }

    private Map<String, String> fetchUserDetailsFromGoogle(String accessToken) {
        try {
            // Google userinfo endpoint
            String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";

            // Send the request to Google's userinfo endpoint
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(userInfoUrl + "?access_token=" + accessToken, String.class);

            // Parse the response to extract user details
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> userDetails = mapper.readValue(response, Map.class);
            return userDetails;
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch user details from Google", e);
        }
    }
}

