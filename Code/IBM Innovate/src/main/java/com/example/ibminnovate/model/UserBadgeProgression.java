package com.example.ibminnovate.model;

import jakarta.persistence.*;

@Entity
public class UserBadgeProgression {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private BadgeProgression progression;

    private int currentProgress;
    private int highestUnlockedLevel;

    public UserBadgeProgression() {
    }

    public UserBadgeProgression(User user, BadgeProgression progression, int currentProgress, int highestUnlockedLevel) {
        this.user = user;
        this.progression = progression;
        this.currentProgress = currentProgress;
        this.highestUnlockedLevel = highestUnlockedLevel;
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

    public BadgeProgression getProgression() {
        return progression;
    }

    public void setProgression(BadgeProgression progression) {
        this.progression = progression;
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.currentProgress = currentProgress;
    }

    public int getHighestUnlockedLevel() {
        return highestUnlockedLevel;
    }

    public void setHighestUnlockedLevel(int highestUnlockedLevel) {
        this.highestUnlockedLevel = highestUnlockedLevel;
    }
}
