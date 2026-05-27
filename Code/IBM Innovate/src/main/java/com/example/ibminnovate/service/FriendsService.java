package com.example.ibminnovate.service;

import com.example.ibminnovate.model.Friends;
import com.example.ibminnovate.model.User;
import com.example.ibminnovate.repo.FriendsRepository;
import com.example.ibminnovate.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FriendsService {

    @Autowired
    private FriendsRepository friendsRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Friends> getFriends(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Fetch only records where the logged-in user is the actual owner of the record
        List<Friends> friendships = friendsRepository.findByUser(user);

        // Populate profile pictures
        friendships.forEach(friendship -> {
            User friend = userRepository.findByUsername(friendship.getFriendUsername())
                    .orElse(null);
            if (friend != null) {
                friendship.setFriendProfilePicture(friend.getProfilePicture());
            }
        });

        return friendships;
    }


    // Send a friend request
    public String sendFriendRequest(Long userId, String friendUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        User friend = userRepository.findByUsername(friendUsername)
                .orElseThrow(() -> new RuntimeException("Friend not found"));

        // Outgoing request
        Friends outgoing = new Friends();
        outgoing.setUser(user);
        outgoing.setFriendUsername(friendUsername);
        outgoing.setStatus("PENDING");
        outgoing.setDirection(Friends.FriendshipDirection.SENT);
        friendsRepository.save(outgoing);

        // Incoming request for the recipient
        Friends incoming = new Friends();
        incoming.setUser(friend);
        incoming.setFriendUsername(user.getUsername());
        incoming.setStatus("PENDING");
        incoming.setDirection(Friends.FriendshipDirection.RECEIVED);
        friendsRepository.save(incoming);

        return "Request sent to " + friendUsername;
    }

    // Accept a friend request
    public void acceptFriendRequest(Long friendshipId) {
        Friends friendship = friendsRepository.findById(friendshipId)
                .orElseThrow(() -> new RuntimeException("Friend request not found"));

        // Update incoming request
        friendship.setStatus("ACCEPTED");
        friendsRepository.save(friendship);

        // Update corresponding outgoing request
        User recipient = userRepository.findByUsername(friendship.getFriendUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Friends outgoing = friendsRepository.findByUserAndFriendUsername(
                        recipient,
                        friendship.getUser().getUsername())
                .orElseThrow(() -> new RuntimeException("Outgoing request not found"));

        outgoing.setStatus("ACCEPTED");
        friendsRepository.save(outgoing);
    }

    // Add leaderboard method
    public List<User> getFriendLeaderboard(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get all accepted friends
        List<Friends> friendships = friendsRepository.findByUserAndStatus(user, "ACCEPTED");
        List<String> friendUsernames = friendships.stream()
                .map(Friends::getFriendUsername)
                .collect(Collectors.toList());

        // Add the current user's username to the list
        friendUsernames.add(user.getUsername());

        // Fetch all users by username, including self + friends
        return userRepository.findByUsernameIn(friendUsernames)
                .stream()
                .sorted(Comparator.comparingInt(User::getExp).reversed())
                .collect(Collectors.toList());
    }

    // Remove a friend or pending request
    public void removeFriendRequest(Long userId, String friendUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Optional<Friends> friendship = friendsRepository.findByUserAndFriendUsername(user, friendUsername);
        friendship.ifPresent(friendsRepository::delete);
    }
}