package com.example.ibminnovate.service;

import com.example.ibminnovate.model.Achievement;
import com.example.ibminnovate.model.User;
import com.example.ibminnovate.repo.AchievementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AchievementService {

    @Autowired
    private AchievementRepository achievementRepository;

    public List<Achievement> getAchievementsByUser(User user) {
        return achievementRepository.findByUser(user);
    }
}