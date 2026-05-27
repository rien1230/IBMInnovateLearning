package com.example.ibminnovate.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class UserBadge {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Badge badge;

    private LocalDateTime earnedAt;

    public UserBadge() {}

    public UserBadge(User user, Badge badge, LocalDateTime earnedAt) {
        this.user = user;
        this.badge = badge;
        this.earnedAt = earnedAt;
    }

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

    public Badge getBadge() {
        return badge;
    }

    public void setBadge(Badge badge) {
        this.badge = badge;
    }

    public LocalDateTime getEarnedAt() {
        return earnedAt;
    }

    public void setEarnedAt(LocalDateTime earnedAt) {
        this.earnedAt = earnedAt;
    }
}
