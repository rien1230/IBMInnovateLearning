package com.example.ibminnovate.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_leaderboard")
public class UserLeaderboard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private int exp = 0;
    private int rank;

    // Constructors
    public UserLeaderboard() {}

    public UserLeaderboard(String username, int exp) {
        this.username = username;
        this.exp = exp;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public int getExp() { return exp; }
    public int getRank() { return rank; }

    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setExp(int exp) { this.exp = exp; }
    public void setRank(int rank) { this.rank = rank; }
}