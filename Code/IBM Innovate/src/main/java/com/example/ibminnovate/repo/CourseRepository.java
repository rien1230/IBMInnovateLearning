package com.example.ibminnovate.repo;

import com.example.ibminnovate.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    // Custom queries can be added if needed
    Course findByCourseName(String courseName);
    List<Course> findAll();

    Course findById(long id);

    //Search courses by name or tag
    List<Course> findByCourseNameContainingOrTagsContaining(String courseName, String tags);

    List<Course> findByDurationContaining(String duration);

    List<Course> findByLanguagesIn(List<String> languages);

    // Custom query to search courses by tag
    @Query("SELECT c FROM Course c JOIN c.tags t WHERE t = :tag")
    List<Course> findByTag(@Param("tag") String tag);



    // Custom query to search courses by multiple tags
    @Query("SELECT c FROM Course c JOIN c.tags t WHERE t IN :tags")
    List<Course> findByTagsIn(@Param("tags") List<String> tags);

    // Search courses by name (partial match)
    List<Course> findByCourseNameContaining(String courseName);

    @Query("SELECT c FROM Course c JOIN c.languages l WHERE l = :language")
    List<Course> findByLanguage(@Param("language") String language);

    // Custom query to search courses by multiple languages
    // @Query("SELECT c FROM Course c JOIN c.languages l WHERE l IN :languages")
    // List<Course> findByLanguagesIn(@Param("languages") List<String> languages);

    @Query("SELECT c FROM Course c " +
            "JOIN UserCourse uc ON c.id = uc.course.id " +
            "WHERE uc.user.ID = :userId AND uc.status = 'ongoing'")
    List<Course> findOngoingCoursesByUserId(@Param("userId") Long userId);

    @Query("SELECT c FROM Course c JOIN UserCourse uc ON c.id = uc.course.id WHERE uc.id.userId = :userId AND uc.status = 'completed'")
    List<Course> findCompletedCoursesByUserId(@Param("userId") Long userId);

    Optional<Course> findById(Long id);
}









