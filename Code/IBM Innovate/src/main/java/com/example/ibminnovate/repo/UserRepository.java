package com.example.ibminnovate.repo;

import com.example.ibminnovate.inter.CustomUserRepository;
import com.example.ibminnovate.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {
    Optional<User> findByUsername(String username); // Corrected method name

    Optional<User> findByEmail(String email);

    Optional<User> findByID(Long ID);// Corrected method name

    Optional<String> findUsernameByID(Long ID);

    Optional<User> findByEmailAndPassword(String email, String password);

    List<User> findByUsernameIn(List<String> usernames);
    List<User> findAllByOrderByExpDesc();
}