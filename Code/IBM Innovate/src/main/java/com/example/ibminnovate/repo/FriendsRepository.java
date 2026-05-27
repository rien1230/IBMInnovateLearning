package com.example.ibminnovate.repo;

import com.example.ibminnovate.model.Friends;
import com.example.ibminnovate.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendsRepository extends JpaRepository<Friends, Long> {
    // Find all friends and pending requests for a user
    List<Friends> findByUserOrFriendUsername(User user, String friendUsername);

    // Find a specific friend request
    Optional<Friends> findByUserAndFriendUsername(User user, String friendUsername);
    List<Friends> findByUserAndStatus(User user, String status);
    List<Friends> findByFriendUsernameAndStatus(String friendUsername, String status);

    List<Friends> findByUser(User user);}