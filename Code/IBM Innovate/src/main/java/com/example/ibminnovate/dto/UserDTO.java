package com.example.ibminnovate.dto;

public class UserDTO {
    private String username;
    private int exp;

    public UserDTO(String username, int exp) {
        this.username = username;
        this.exp = exp;
    }

    public String getUsername() {
        return username;
    }

    public int getExp() {
        return exp;
    }
}
