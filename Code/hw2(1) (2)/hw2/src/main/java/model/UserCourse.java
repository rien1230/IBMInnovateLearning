package model;
import jakarta.persistence.MapsId;

import java.time.LocalDateTime;

public class UserCourse {
    @MapsId("userId")
    private User user;
    @MapsId("ID")
    private Course courses;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}