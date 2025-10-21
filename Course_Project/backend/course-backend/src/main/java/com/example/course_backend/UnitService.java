

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

    public Unit createUnit(Unit unit) {
        return unitRepository.save(unit);
    }

    public List<Unit> getAllUnits() {
        return unitRepository.findAll();
    }

    public List<Unit> getUnitsByCourseId(UUID courseId) {
        Optional<Course> courseOpt = courseRepository.findById(courseId);
        return courseOpt.map(Course::getUnits).orElse(Collections.emptyList());
    }

    public Unit addUnitToCourse(UUID courseId, Unit unit) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        unit.setCourse(course);
        course.getUnits().add(unit);
        courseRepository.save(course);
        return unit;
    }

    public Unit updateUnit(UUID courseId, UUID unitId, Unit updatedUnit) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Unit not found"));
        if (!unit.getCourse().getId().equals(courseId)) {
            throw new RuntimeException("Unit does not belong to course " + courseId);
        }
        unit.setTitle(updatedUnit.getTitle());
        unit.setContent(updatedUnit.getContent());
        return unitRepository.save(unit);
    }

    public Unit patchUnit(UUID courseId, UUID unitId, Map<String, Object> updates) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Unit not found"));
        if (!unit.getCourse().getId().equals(courseId)) {
            throw new RuntimeException("Unit does not belong to course " + courseId);
        }
        updates.forEach((key, value) -> {
            switch (key) {
                case "title" -> unit.setTitle((String) value);
                case "content" -> unit.setContent((String) value);
            }
        });
        return unitRepository.save(unit);
    }

    public void deleteUnit(UUID courseId, UUID unitId) {
        Unit unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new RuntimeException("Unit not found"));
        if (!unit.getCourse().getId().equals(courseId)) {
            throw new RuntimeException("Unit does not belong to course " + courseId);
        }
        unitRepository.delete(unit);
    }
}

