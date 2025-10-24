

package com.example.course_backend;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UnitService {

    private final UnitRepository unitRepository;
    private final CourseRepository courseRepository;
    private static final Logger logger = LoggerFactory.getLogger(UnitService.class);

    public UnitService(UnitRepository unitRepository, CourseRepository courseRepository) {
        this.unitRepository = unitRepository;
        this.courseRepository = courseRepository;
    }

    public Unit addUnitToCourse(UUID courseId, Unit unit) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found"));

        if (unit.getTitle() == null || unit.getTitle().isBlank())
            throw new BadRequestException("Unit title is required");
        if (unit.getContent() == null || unit.getContent().isBlank())
            throw new BadRequestException("Unit content is required");

        unit.setCourse(course); // link unit to course
        Unit savedUnit = unitRepository.save(unit); // save unit explicitly

        // Optionally, add to course units list (not mandatory if mappedBy is correct)
        course.getUnits().add(savedUnit);

        logger.info("Added unit '{}' to course '{}'", savedUnit.getTitle(), course.getName());
        return savedUnit;
    }

    public Page<Unit> getUnitsByCoursePaginated(UUID courseId, Pageable pageable) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found"));
        List<Unit> units = course.getUnits();
        int start = Math.min((int) pageable.getOffset(), units.size());
        int end = Math.min(start + pageable.getPageSize(), units.size());
        List<Unit> content = units.subList(start, end);
        return new PageImpl<>(content, pageable, units.size());
    }

    public Unit updateUnit(UUID courseId, UUID unitId, Unit updatedUnit) {
        Unit unit = unitRepository.findById(unitId).orElseThrow(() -> new NotFoundException("Unit not found"));
        if (unit.getCourse() == null || !unit.getCourse().getId().equals(courseId))
            throw new BadRequestException("Unit does not belong to course " + courseId);

        if (updatedUnit.getTitle() != null && !updatedUnit.getTitle().isBlank()) unit.setTitle(updatedUnit.getTitle());
        if (updatedUnit.getContent() != null && !updatedUnit.getContent().isBlank()) unit.setContent(updatedUnit.getContent());
        Unit saved = unitRepository.save(unit);
        logger.info("Updated unit: {} (id={})", saved.getTitle(), saved.getId());
        return saved;
    }

    public Unit patchUnit(UUID courseId, UUID unitId, Map<String, Object> updates) {
        Unit unit = unitRepository.findById(unitId).orElseThrow(() -> new NotFoundException("Unit not found"));
        if (unit.getCourse() == null || !unit.getCourse().getId().equals(courseId))
            throw new BadRequestException("Unit does not belong to course " + courseId);

        updates.forEach((k, v) -> {
            switch (k) {
                case "title" -> unit.setTitle((String) v);
                case "content" -> unit.setContent((String) v);
            }
        });

        Unit saved = unitRepository.save(unit);
        logger.info("Patched unit: {} (id={})", saved.getTitle(), saved.getId());
        return saved;
    }

    public void deleteUnit(UUID courseId, UUID unitId) {
        Unit unit = unitRepository.findById(unitId).orElseThrow(() -> new NotFoundException("Unit not found"));
        if (unit.getCourse() == null || !unit.getCourse().getId().equals(courseId))
            throw new BadRequestException("Unit does not belong to course " + courseId);
        unitRepository.delete(unit);
        logger.info("Deleted unit: {} (id={})", unit.getTitle(), unit.getId());
    }
}
