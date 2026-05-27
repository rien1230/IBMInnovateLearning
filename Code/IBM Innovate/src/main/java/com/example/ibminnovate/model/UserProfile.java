package com.example.ibminnovate.model;

import jakarta.persistence.Entity;


public class UserProfile {
    private String username;
    private String email;
    private String imageUrl;
    private String avatarUrl;

    // Constructors, getters, and setters
    public UserProfile(String username, String email, String imageUrl, String avatarUrl) {
        this.username = username;
        this.email = email;
        this.imageUrl = imageUrl;
        this.avatarUrl = avatarUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
