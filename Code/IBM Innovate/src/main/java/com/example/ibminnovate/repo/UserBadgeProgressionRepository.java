package com.example.ibminnovate.repo;

import com.example.ibminnovate.model.BadgeProgression;
import com.example.ibminnovate.model.User;
import com.example.ibminnovate.model.UserBadgeProgression;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserBadgeProgressionRepository extends JpaRepository<UserBadgeProgression, Long> {
    Optional<UserBadgeProgression> findByUserAndProgression(User user, BadgeProgression progression);
}