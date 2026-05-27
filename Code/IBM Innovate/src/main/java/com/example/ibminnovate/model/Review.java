package com.example.ibminnovate.model;

public class Review {
    private String username;
    private String text;
    private int rating;
    private String courseName; // Adding course name instead of course number

    // Default constructor
    public Review() {}

    // Constructor with parameters
    public Review(String username, String text, int rating, String courseName) {
        this.username = username;
        this.text = text;
        this.rating = rating;
        this.courseName = courseName;
    }

    // Getters and setters
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

    public String getText() {return text;}
    public void setText(String text) {this.text = text;}

    public int getRating() {return rating;}
    public void setRating(int rating) {this.rating = rating;}

    public String getCourseName() {return courseName;}
    public void setCourseName(String courseName) {this.courseName = courseName;}
}
