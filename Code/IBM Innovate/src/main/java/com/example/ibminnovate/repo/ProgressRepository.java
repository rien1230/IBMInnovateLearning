package com.example.ibminnovate.repo;

import com.example.ibminnovate.model.Progress;
import com.example.ibminnovate.model.ProgressId;
import com.example.ibminnovate.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {
    List<Progress> findByUser(User user);
    @Query("SELECT p FROM Progress p WHERE p.user = :user AND p.course.courseName = :courseName")
    Progress findByUserAndCourseName(@Param("user") User user, @Param("courseName") String courseName);

    Optional<Progress> findById(ProgressId progressId);
    // Find all Progress entries for a specific user
    @Query("SELECT p FROM Progress p WHERE p.user.ID = :userId")
    List<Progress> findByUserId(@Param("userId") Long userId);

    // Find all Progress entries for a specific course
    @Query("SELECT p FROM Progress p WHERE p.course.id = :courseId")
    List<Progress> findByCourseId(@Param("courseId") Long courseId);

    // Find all Progress entries for a specific user and course
    @Query("SELECT p FROM Progress p WHERE p.user.ID = :userId AND p.course.id = :courseId")
    Optional<Progress> findByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    // Find all Progress entries with a specific status for a user
    @Query("SELECT p FROM Progress p WHERE p.user.ID = :userId AND p.status = :status")
    List<Progress> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    // Method to count courses with a specific status
    int countByUserIDAndStatus(Long userId, String status);

    // Method to count courses with status in a list of values
    int countByUserIDAndStatusIn(Long userId, List<String> statuses);

}

