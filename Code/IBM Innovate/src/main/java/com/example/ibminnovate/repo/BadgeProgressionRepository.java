package com.example.ibminnovate.repo;

import com.example.ibminnovate.model.BadgeProgression;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BadgeProgressionRepository extends JpaRepository<BadgeProgression, Long> {
    Optional<BadgeProgression> findByName(String name);
}