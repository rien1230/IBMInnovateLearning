package com.example.project_code_2.controller;

import com.example.project_code_2.model.User;
import com.example.project_code_2.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MyController {

    @Autowired
    private UserService userService;

    // Display the profile update form
    @GetMapping("/profile/update")
    public String showProfileUpdateForm(Model model) {
        User currentUser = userService.getCurrentUser();
        model.addAttribute("user", currentUser);
        return "update-profile"; // Thymeleaf template
    }

    // Handle profile update form submission
    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute User user) {
        userService.updateUserDetails(user); // Update user in DB
        return "redirect:/"; // Redirect to main page
    }

    // Main page after successful profile update
    @GetMapping("/")
    public String home() {
        return "home"; // Thymeleaf template for home page
    }
}

