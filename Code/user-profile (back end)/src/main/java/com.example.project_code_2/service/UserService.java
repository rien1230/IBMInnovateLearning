package com.example.project_code_2.service;

import com.example.project_code_2.model.User;
import com.example.project_code_2.repo.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Retrieve the currently authenticated user
    public User getCurrentUser() {
        String username = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
        return userRepository.findByUsername(username);
    }

    // Update user details
    public void updateUserDetails(User user) {
        User existingUser = getCurrentUser();

        if (user.getUsername() != null) {
            existingUser.setUsername(user.getUsername());}
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());}
        if (user.getPassword() != null) {
            existingUser.setPassword(user.getPassword());}

        userRepository.save(existingUser); // Save updated user to database
    }
}

