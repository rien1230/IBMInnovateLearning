package com.example.ibminnovate.service;

import com.example.ibminnovate.model.User;
import com.example.ibminnovate.model.UserStreak;
import com.example.ibminnovate.repo.UserRepository;
import com.example.ibminnovate.repo.UserStreakRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class UserStreakService {

    @Autowired
    private UserStreakRepository userStreakRepository;

    @Autowired
    private UserRepository userRepository;

    public void updateStreak(Long userId) {
        Optional<User> userOpt = userRepository.findByID(userId);
        if (userOpt.isEmpty()) {
            return; // User not found
        }

        User user = userOpt.get();
        LocalDate today = LocalDate.now();

        //Find most recent streak entry
        Optional<UserStreak> latestStreakOpt = userStreakRepository.findTopByUserOrderByStreakDateDesc(user);

        if (latestStreakOpt.isPresent()) {
            UserStreak latestStreak = latestStreakOpt.get();

            if (latestStreak.getStreakDate().equals(today)) {
                return; //User already has streak entry for today
            } else if (latestStreak.getStreakDate().plusDays(1).equals(today)) {
                int newStreakCount = latestStreak.getStreakCount() + 1; //Extend streak
                userStreakRepository.save(new UserStreak(user, newStreakCount, today));
            } else {
                userStreakRepository.save(new UserStreak(user, 1, today)); //Reset broken streak
            }
        } else {
            userStreakRepository.save(new UserStreak(user, 1, today)); //A user's first streak entry
        }
    }

    public int getCurrentStreak(Long userId) {
        Optional<User> userOpt = userRepository.findByID(userId);
        if (userOpt.isEmpty()) {
            return 0;
        }

        //Retrieve user's latest streak
        User user = userOpt.get();
        Optional<UserStreak> latestStreakOpt = userStreakRepository.findTopByUserOrderByStreakDateDesc(user);
        return latestStreakOpt.map(UserStreak::getStreakCount).orElse(0);
    }
}
