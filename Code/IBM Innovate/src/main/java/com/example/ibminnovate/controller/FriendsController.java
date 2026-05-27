package com.example.ibminnovate.controller;

import com.example.ibminnovate.service.FriendsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FriendsController {

    @Autowired
    private FriendsService friendsService;


    @GetMapping("/friends")
    public String getFriendsPage(@RequestParam Long userId, Model model) {
        System.out.println("Fetching friends for userId: " + userId);

        var friends = friendsService.getFriends(userId);
        boolean hasPendingRequests = friends.stream()
                .anyMatch(f -> "PENDING".equalsIgnoreCase(f.getStatus()));



        model.addAttribute("friends", friends);
        model.addAttribute("friendLeaderboard", friendsService.getFriendLeaderboard(userId));
        model.addAttribute("userId", userId);
        model.addAttribute("hasPendingRequests", hasPendingRequests);

        return "friends";
    }


    // Endpoint to send a friend request
    @PostMapping("/friends/request")
    public String sendFriendRequest(
            @RequestParam Long userId,
            @RequestParam String friendUsername,
            RedirectAttributes redirectAttributes) {

        try {
            // Send the friend request and get the success message
            String message = friendsService.sendFriendRequest(userId, friendUsername);
            redirectAttributes.addFlashAttribute("message", message);
        } catch (RuntimeException e) {
            // Handle errors (e.g., request already sent)
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        // Redirect back to the friends page
        return "redirect:/friends?userId=" + userId;
    }

    // Endpoint to remove a friend or pending request
    @PostMapping("/friends/remove")
    public String removeFriend(@RequestParam Long userId, @RequestParam String friendUsername) {
        friendsService.removeFriendRequest(userId, friendUsername);
        return "redirect:/friends?userId=" + userId; // Redirect back to the friends page
    }

    // Endpoint to accept a friend request
    @PostMapping("/friends/accept")
    public String acceptFriendRequest(
            @RequestParam Long userId,
            @RequestParam Long friendshipId,
            RedirectAttributes redirectAttributes) {

        try {
            friendsService.acceptFriendRequest(friendshipId);
            redirectAttributes.addFlashAttribute("message", "Friend request accepted");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/friends?userId=" + userId;
    }}