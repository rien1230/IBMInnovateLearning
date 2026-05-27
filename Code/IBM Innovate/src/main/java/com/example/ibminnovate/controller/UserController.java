package com.example.ibminnovate.controller;

import com.example.ibminnovate.dto.ValidateOTPRequest;
import com.example.ibminnovate.model.LoginRequest;
import com.example.ibminnovate.model.User;
import com.example.ibminnovate.repo.UserRepository;
import com.example.ibminnovate.service.UserService;
import com.example.ibminnovate.utility.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.example.ibminnovate.service.EmailService;

import java.util.*;

@Controller
public class UserController {
    private final UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inject BCryptPasswordEncoder

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/users/{userId}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long userId,
                                                           @RequestHeader("Authorization") String token) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Validate token
            Claims claims = jwtUtil.validateToken(token.replace("Bearer ", ""));
            String email = claims.getSubject();

            // Verify the requested user matches the token owner (security check)
            Optional<User> requestingUser = userRepository.findByEmail(email);
            if (!requestingUser.isPresent() || requestingUser.get().getID() != userId.longValue()) {
                response.put("error", "Unauthorized access");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                response.put("userId", user.getID());
                response.put("email", user.getEmail());
                response.put("username", user.getUsername());
                response.put("exp", user.getExp()); // Make sure your User entity has this field
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("error", "Invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUser(@RequestHeader("Authorization") String token) {
        Map<String, Object> userDetails = new HashMap<>();

        System.out.println("Received token: " + token); // Debugging step

        try {
            // Validate the token
            Claims claims = jwtUtil.validateToken(token.replace("Bearer ", ""));
            String email = claims.getSubject();

            // Fetch user details from the database
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                userDetails.put("userId", user.getID());
                userDetails.put("email", user.getEmail());
                userDetails.put("username", user.getUsername());
                userDetails.put("userType", user.getUserType()); // "regular" or "google"
                return ResponseEntity.ok(userDetails);
            } else {
                userDetails.put("error", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(userDetails);
            }
        } catch (Exception e) {
            userDetails.put("error", "Invalid token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userDetails);
        }
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Renders login.html from templates
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        Optional<User> user = userRepository.findByEmail(loginRequest.getEmail());

        if (user.isPresent() && passwordEncoder.matches(loginRequest.getPassword(), user.get().getPassword())) {
            String token = jwtUtil.generateToken(user.get().getEmail());
            Map<String, Object> response = new HashMap<>();
            response.put("userId", user.get().getID());
            response.put("username", user.get().getUsername());
            response.put("email", user.get().getEmail());
            response.put("token", token);
            response.put("userType", user.get().getUserType()); // Include userType in the response
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("message", "Invalid credentials"));
        }
    }


    @GetMapping("/users")
    @ResponseBody // Ensures the response is returned as JSON
    public List<User> getAllUsers() {
        return userRepository.findAll(); // Returns a list of users
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "register"; // Renders register.html from templates
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam String username,
            Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match");
            return "register"; // Stay on the register page
        }

        // Create a new regular user
        User user = new User();
        user.setEmail(email);
        user.setPassword(password); // Password will be hashed in the service layer
        user.setUsername(username);

        try {
            userService.registerUser(user); // Register the user
            model.addAttribute("message", "Registration successful");
            return "login"; // Redirect to login page
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register"; // Stay on register page with error message
        }
    }

    @GetMapping("/password") // New endpoint for password reset
    public String showPasswordResetPage() {
        return "passwordreset"; // Renders passwordreset.html from templates
    }

    //Mapping that sends the otp to the email specified in the frontend
    @PostMapping("/send-otp")
    @ResponseBody
    public Map<String, Boolean> sendOtp(@RequestBody Map<String, String> request) {
        Map<String, Boolean> response = new HashMap<>();
        try {
            String email = request.get("email");
            if (email == null || email.isEmpty()) {
                response.put("success", false);
                return response;
            }
            System.out.println("Sending OTP to email: " + email);
            emailService.sendOtpEmail(email);
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            // Log the exception for debugging
            e.printStackTrace();
        }
        return response;
    }

    //Mapping that validates the otp typed in
    @PostMapping("/validate-otp")
    public ResponseEntity<Map<String, Boolean>> validateOTP(@RequestBody ValidateOTPRequest request) {
        boolean isValid = emailService.validateOTP(request.getEmail(), request.getOtp());
        Map<String, Boolean> response = new HashMap<>();
        response.put("success", isValid);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request, HttpServletResponse response) {
        // Invalidate the session
        request.getSession().invalidate();

        // Clear any authentication tokens (if using JWT, you may need to blacklist the token)
        SecurityContextHolder.clearContext();

        // Clear the JSESSIONID cookie (if using session-based authentication)
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Delete the cookie
        response.addCookie(cookie);

        // Return success response
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("message", "Logged out successfully");
        return ResponseEntity.ok(responseBody);
    }



}