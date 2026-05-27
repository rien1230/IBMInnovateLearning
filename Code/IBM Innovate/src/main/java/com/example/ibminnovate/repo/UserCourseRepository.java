package com.example.ibminnovate.repo;

import com.example.ibminnovate.model.Course;
import com.example.ibminnovate.model.UserCourse;
import com.example.ibminnovate.model.UserCourseId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserCourseRepository extends JpaRepository<UserCourse, UserCourseId> {

    @Query("SELECT uc FROM UserCourse uc JOIN FETCH uc.course WHERE uc.id.userId = :userId AND uc.status = :status")
    List<UserCourse> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") String status);

    // Find all courses for a specific user
    @Query("SELECT uc FROM UserCourse uc WHERE uc.id.userId = :userId")
    List<UserCourse> findByUserId(@Param("userId") Long userId);

    // Find all users for a specific course
    @Query("SELECT uc FROM UserCourse uc WHERE uc.id.courseId = :courseId")
    List<UserCourse> findByCourseId(@Param("courseId") Long courseId);

    // Find a specific UserCourse entry using the composite key
    @Query("SELECT uc FROM UserCourse uc WHERE uc.id.userId = :userId AND uc.id.courseId = :courseId")
    Optional<UserCourse> findByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    // Fetch ongoing courses for a user
    @Query("SELECT uc FROM UserCourse uc WHERE uc.user.ID = :userId AND uc.status IN ('ongoing', 'paused')")
    List<UserCourse> findOngoingCoursesByUserId(@Param("userId") Long userId);

    // Fetch completed courses for a user
    @Query("SELECT uc FROM UserCourse uc JOIN FETCH uc.course WHERE uc.user.ID = :userId AND uc.status = 'completed'")
    List<UserCourse> findCompletedCoursesByUserId(@Param("userId") Long userId);

    Optional<UserCourse> findById(UserCourseId userCourseId);

    @Modifying
    @Query("UPDATE UserCourse uc SET uc.status = :status, uc.endTime = :endTime WHERE uc.user.ID = :userId AND uc.course.id = :courseId")
    void updateCourseStatus(@Param("userId") Long userId, @Param("courseId") Long courseId, @Param("status") String status, @Param("endTime") LocalDateTime endTime);

    boolean existsByUserIDAndCourseIdAndStatus(Long userId, Long courseId, String status);

    // Save a new UserCourse entry
    UserCourse save(UserCourse userCourse);

    @Query("SELECT uc FROM UserCourse uc WHERE uc.user.ID = :userId AND uc.status IN :statuses")
    List<UserCourse> findByUserIdAndStatusIn(@Param("userId") Long userId, @Param("statuses") List<String> statuses);

}
