

package com.example.course_backend;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UnitRepository extends JpaRepository<Unit, UUID> {
    List<Unit> findByCourseId(UUID courseId);
}


