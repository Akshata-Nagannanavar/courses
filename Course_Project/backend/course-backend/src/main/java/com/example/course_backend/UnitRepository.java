

package com.example.course_backend;

import com.example.course_backend.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface UnitRepository extends JpaRepository<Unit, UUID> {
    List<Unit> findByCourseId(UUID courseId);
}


