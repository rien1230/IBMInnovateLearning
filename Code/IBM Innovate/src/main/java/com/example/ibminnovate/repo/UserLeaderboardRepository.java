package com.example.ibminnovate.repo;

import com.example.ibminnovate.model.UserLeaderboard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserLeaderboardRepository extends JpaRepository<UserLeaderboard, Long> {

    @Query("SELECT u FROM UserLeaderboard u ORDER BY u.exp DESC")
    List<UserLeaderboard> findAllOrderByExpDesc();
}