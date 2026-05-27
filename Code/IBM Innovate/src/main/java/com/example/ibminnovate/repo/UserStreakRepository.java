package com.example.ibminnovate.repo;

import com.example.ibminnovate.model.User;
import com.example.ibminnovate.model.UserStreak;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserStreakRepository extends JpaRepository<UserStreak, Long> {
    //Find user's most recent streak record
    Optional<UserStreak> findTopByUserOrderByStreakDateDesc(User user);
}
