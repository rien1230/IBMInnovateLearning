package com.example.ibminnovate.service;

import com.example.ibminnovate.model.User;
import com.example.ibminnovate.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Use dependency injection

    // Register a regular user
    public User registerUser(User user) {
        // Check if email already exists
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email is already registered.");
        }

        // Encrypt the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Set userType to "regular"
        user.setUserType("regular");

        // Save the user to the database
        return userRepository.save(user);
    }

    // Register a Google user
    public User registerGoogleUser(String email, String name) {
        // Check if the user already exists
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return existingUser.get(); // Return the existing user
        }

        // Create a new Google user
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUsername(name);
        newUser.setUserType("google"); // Set userType to "google"
        newUser.setRole("USER"); // Set the role (if applicable)

        // Save the new user to the database
        return userRepository.save(newUser);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Long getCurrentUserId() {
        // Get the authentication object from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        if (authentication != null && authentication.isAuthenticated()) {
            // Get the principal (authenticated user)
            Object principal = authentication.getPrincipal();

            // Check if the principal is an instance of your User model
            if (principal instanceof User) {
                User user = (User) principal;
                return user.getID(); // Use the User model's getId() method
            }
        }

        // If no authenticated user is found, throw an exception or return null
        throw new RuntimeException("No authenticated user found");
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User getCurrentUser(String username) {
        // Fetch the user by username
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
    }


    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        return authentication.getName(); // Returns the username
    }

    public void updateName(Long userId, String name) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(name);
        userRepository.save(user);
    }

    public void updateEmail(Long userId, String email) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setEmail(email);
        userRepository.save(user);
    }

    public void updatePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }


    public String getCurrentUsername() {
        return userRepository.getCurrentUsername(); // Call the custom method
    }

    public void updateProfilePicture(String username, String profilePicture) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setProfilePicture(profilePicture);
        userRepository.save(user);
    }

    public String getProfilePicture(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getProfilePicture();
    }


    public String getEmail(String username) {
        // Fetch the user's email from the database
        return userRepository.getCurrentEmail(username); // Replace with actual logic
    }



    public String getAvatarUrl(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(User::getAvatarUrl).orElse("default-avatar.png");
    }

    public void saveAvatar(String username, String avatarUrl) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setAvatarUrl(avatarUrl);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

}