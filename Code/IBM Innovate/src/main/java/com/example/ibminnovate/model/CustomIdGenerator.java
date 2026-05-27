package com.example.ibminnovate.model;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.Random;

public class CustomIdGenerator implements IdentifierGenerator {

    private static final Random RANDOM = new Random();

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        long id;
        do {
            id = 100000 + RANDOM.nextInt(900000); // Generates a 6-digit number (100000 - 999999)
        } while (idExists(session, id));
        return id;
    }

    private boolean idExists(SharedSessionContractImplementor session, long id) {
        return session.createQuery("SELECT 1 FROM UserLeaderboard WHERE ID = :id")
                .setParameter("id", id)
                .uniqueResult() != null;
    }
}
