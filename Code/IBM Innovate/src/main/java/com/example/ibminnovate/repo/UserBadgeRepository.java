package com.example.ibminnovate.repo;

import com.example.ibminnovate.model.Badge;
import com.example.ibminnovate.model.User;
import com.example.ibminnovate.model.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    boolean existsByUserAndBadge(User user, Badge badge);
}
