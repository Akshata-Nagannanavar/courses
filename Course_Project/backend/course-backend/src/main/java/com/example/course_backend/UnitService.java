
package com.example.course_backend;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.UUID;

@Service
public class UnitService {

    private final UnitRepository unitRepository;
    private final CourseRepository courseRepository;

    public UnitService(UnitRepository unitRepository, CourseRepository courseRepository) {
        this.unitRepository = unitRepository;
        this.courseRepository = courseRepository;
    }

    // Save a standalone unit
    public Unit createUnit(Unit unit) {
        return unitRepository.save(unit);
    }

    // Fetch all units
    public List<Unit> getAllUnits() {
        return unitRepository.findAll();
    }

    // Fetch units for a specific course
    public List<Unit> getUnitsByCourseId(UUID courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        return courseOpt.map(Course::getUnits).orElse(Collections.emptyList());
    }

    // Add a unit to a specific course
    public Unit addUnitToCourse(UUID courseId, Unit unit) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        unit.setCourse(course);           // link unit to course
        course.getUnits().add(unit);      // add unit to course's list
        courseRepository.save(course);    // cascade saves the unit
        return unit;                      // return the saved unit
    }
}

