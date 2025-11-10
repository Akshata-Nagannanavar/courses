

package com.example.course_backend;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface UnitRepository extends JpaRepository<Unit, Integer> {
    List<Unit> findByCourseId(Integer courseId);
}


