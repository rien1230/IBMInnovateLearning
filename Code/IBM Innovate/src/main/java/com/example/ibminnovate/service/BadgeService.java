package com.example.ibminnovate.service;

import com.example.ibminnovate.model.*;
import com.example.ibminnovate.repo.BadgeProgressionRepository;
import com.example.ibminnovate.repo.UserBadgeProgressionRepository;
import com.example.ibminnovate.repo.UserBadgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BadgeService {
    @Autowired
    private BadgeProgressionRepository badgeProgressionRepo;

    @Autowired 
    private UserBadgeProgressionRepository userBadgeProgressionRepo;

    @Autowired 
    private UserBadgeRepository userBadgeRepo;

    public void checkAndAwardBadges(User user, BadgeProgression progression) {
        // Get or create user's progression tracking
        UserBadgeProgression userBadgeProgress = userBadgeProgressionRepo
                .findByUserAndProgression(user, progression)
                .orElseGet(() -> new UserBadgeProgression(user, progression, 0, 0));

        //check all badges in the progression
        for (Badge badge : progression.getBadges()) {
            // skip if already awarded or not yet unlocked
            if (userBadgeProgress.getHighestUnlockedLevel() >= badge.getLevel() ||
                    userBadgeRepo.existsByUserAndBadge(user, badge)) {
                continue;
            }

            //award badge if progress meets threshold
            if (userBadgeProgress.getCurrentProgress() >= badge.getLevel()) {
                userBadgeRepo.save(new UserBadge(user, badge, LocalDateTime.now()));
                userBadgeProgress.setHighestUnlockedLevel(badge.getLevel());
                userBadgeProgressionRepo.save(userBadgeProgress);
            }
        }
    }
}
