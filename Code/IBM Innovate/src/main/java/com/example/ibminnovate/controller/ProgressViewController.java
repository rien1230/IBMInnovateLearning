package com.example.ibminnovate.controller;

import com.example.ibminnovate.model.User;
import com.example.ibminnovate.repo.UserRepository;
import com.example.ibminnovate.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProgressViewController {

    @Autowired
    UserService userService;

    @GetMapping("/progress")
    public String showProgressPage() {
        return "index"; // Looks for src/main/resources/templates/index.html
    }

    @GetMapping("/current-user-id")
    public ResponseEntity<Long> getCurrentUserId() {
        Long userId = userService.getCurrentUserId();
        return ResponseEntity.ok(userId);
    }
}

