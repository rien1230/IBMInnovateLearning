package com.example.ibminnovate.dto;

public class ExpRequest {
    private Long userId;
    private int exp = 0;
    private int level = 1;

    // Getters and setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public int getExp() { return exp; }
    public void setExp(int exp) { this.exp = exp; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
}
