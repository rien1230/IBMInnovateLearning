package com.example.ibminnovate.model;

import jakarta.persistence.*;

@Entity
public class Friends {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //User Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    //got to manually input username into Friends database.
    private String friendUsername;
    private String status;
    private String friendProfilePicture = "default.jpg"; // Default image

    @Enumerated(EnumType.STRING)
    private FriendshipDirection direction; // Add this field

    public enum FriendshipDirection {
        SENT, RECEIVED
    }
// Add getter and setter

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFriendUsername() {
        return friendUsername;
    }

    public void setFriendUsername(String friendUsername) {
        this.friendUsername = friendUsername;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        if (!status.equals("PENDING") && !status.equals("ACCEPTED")) {
            throw new IllegalArgumentException("Invalid status. Must be 'PENDING' or 'ACCEPTED'.");
        }
        this.status = status;
    }
    public String getFriendProfilePicture() {
        return friendProfilePicture;
    }

    public void setFriendProfilePicture(String friendProfilePicture) {
        this.friendProfilePicture = friendProfilePicture;
    }

    public FriendshipDirection getDirection() {
        return direction;
    }

    public void setDirection(FriendshipDirection direction) {
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "Friends{" +
                "id=" + id +
                ", user=" + user +
                ", friendUsername='" + friendUsername + '\'' +
                ", status='" + status + '\'' +
                ", friendProfilePicture='" + friendProfilePicture + '\'' +
                ", direction=" + direction +
                '}';
    }
    }
