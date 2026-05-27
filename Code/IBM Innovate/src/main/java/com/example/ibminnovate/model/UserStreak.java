package com.example.ibminnovate.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class UserStreak {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private int streakCount;
    private LocalDate streakDate;

    public UserStreak() {}

    public UserStreak(User user, int streakCount, LocalDate streakDate) {
        this.user = user;
        this.streakCount = streakCount;
        this.streakDate = streakDate;
    }

    public int getId() {
        return id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public int getStreakCount() {
        return streakCount;
    }

    public void setStreakCount(int streakCount) {
        this.streakCount = streakCount;
    }

    public LocalDate getStreakDate() {
        return streakDate;
    }

    public void setStreakDate(LocalDate streakDate) {
        this.streakDate = streakDate;
    }
}
