package com.example.ibminnovate.repo;

import com.example.ibminnovate.model.CourseHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseHistoryRepository extends JpaRepository<CourseHistory, Long> {
    List<CourseHistory> findByUserIdAndCourseId(Long userId, Long courseId);
}
