package com.example.ibminnovate.repo;

import com.example.ibminnovate.inter.CustomUserRepository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepositoryImpl implements CustomUserRepository {

    @Autowired
    private EntityManager entityManager;

    @Override
    public String getCurrentUsername() {
        List<String> results = entityManager.createQuery("SELECT u.username FROM UserLeaderboard u", String.class)
                .setMaxResults(1)
                .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public String getCurrentEmail(String username) {
        return entityManager.createQuery(
                        "SELECT u.email FROM User u WHERE u.username = :username", String.class)
                .setParameter("username", username)
                .setMaxResults(1)
                .getSingleResult();
    }

}
