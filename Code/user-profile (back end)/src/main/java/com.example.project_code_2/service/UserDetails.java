package com.example.project_code_2.service;

import com.example.project_code_2.model.User;
import com.example.project_code_2.repo.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetails implements UserDetailsService {
    private UserRepository userRepository;
    public UserDetails(UserRepository userRepository) {this.userRepository = userRepository;}

    // method to check if the user is registered and valid
    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        // returns username and password
        return org.springframework.security.core.userdetails.User.withUsername(user.getUsername()).password(user.getPassword()).build();
    }
}
