package com.example.course_backend;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;




public interface CourseRepository extends JpaRepository<Course, Integer> {
    Page<Course> findAll(Pageable pageable);

}
