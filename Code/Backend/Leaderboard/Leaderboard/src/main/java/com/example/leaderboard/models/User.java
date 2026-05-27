package com.example.leaderboard.models;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private int exp;

    // Constructors
    public User() {}

    public User(String username, int exp) {
        this.username = username;
        this.exp = exp;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public int getExp() { return exp; }

    public void setUsername(String username) { this.username = username; }
    public void setExp(int exp) { this.exp = exp; }
}
