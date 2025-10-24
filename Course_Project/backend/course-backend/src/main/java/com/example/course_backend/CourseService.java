package com.example.course_backend;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import com.example.course_backend.UnitRepository;


@Service
public class CourseService {

    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);

    private final CourseRepository courseRepository;
    private final UnitRepository unitRepository;
    private final MediumListConverter mediumConverter = new MediumListConverter();

    public CourseService(CourseRepository courseRepository,  UnitRepository unitRepository) {

        this.courseRepository = courseRepository;
        this.unitRepository = unitRepository;

    }

    // Create course: validate fields, convert mediums -> medium column
    public Course createCourse(Course course) {
        // Field-level checks (individual messages)
        if (course.getName() == null || course.getName().isBlank())
            throw new BadRequestException("Name is required");
        if (course.getDescription() == null || course.getDescription().isBlank())
            throw new BadRequestException("Description is required");
        if (course.getBoard() == null || course.getBoard().isBlank())
            throw new BadRequestException("Board is required");
        if (course.getMediums() == null || course.getMediums().isEmpty())
            throw new BadRequestException("At least one medium is required");
        if (course.getGrade() == null || course.getGrade().isBlank())
            throw new BadRequestException("Grade is required");
        if (course.getSubject() == null || course.getSubject().isBlank())
            throw new BadRequestException("Subject is required");

        // convert mediums list to CSV string for existing 'medium' column
        course.setMedium(mediumConverter.convertToDatabaseColumn(course.getMediums()));

        if (course.getUnits() == null)
            course.setUnits(new ArrayList<>());

        Course saved = courseRepository.save(course);
        logger.info("Created course: {} (id={})", saved.getName(), saved.getId());
        return saved;
    }

    // Read single
    public Course getCourseById(UUID courseId) {
        Course c = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));
        // populate transient mediums for outgoing JSON
        c.setMediums(mediumConverter.convertToEntityAttribute(c.getMedium()));
        return c;
    }

    // Update whole (PUT) - replace allowed fields, convert mediums if provided
    public Course updateCourse(UUID courseId, Course updatedCourse) {
        Course existing = getCourseById(courseId);

        if (updatedCourse.getName() != null && !updatedCourse.getName().isBlank()) existing.setName(updatedCourse.getName());
        if (updatedCourse.getDescription() != null && !updatedCourse.getDescription().isBlank()) existing.setDescription(updatedCourse.getDescription());
        if (updatedCourse.getBoard() != null && !updatedCourse.getBoard().isBlank()) existing.setBoard(updatedCourse.getBoard());
        if (updatedCourse.getGrade() != null && !updatedCourse.getGrade().isBlank()) existing.setGrade(updatedCourse.getGrade());
        if (updatedCourse.getSubject() != null && !updatedCourse.getSubject().isBlank()) existing.setSubject(updatedCourse.getSubject());

        if (updatedCourse.getMediums() != null && !updatedCourse.getMediums().isEmpty()) {
            existing.setMedium(mediumConverter.convertToDatabaseColumn(updatedCourse.getMediums()));
            existing.setMediums(updatedCourse.getMediums());
        } else {
            // keep existing medium value unchanged if not provided
            existing.setMediums(mediumConverter.convertToEntityAttribute(existing.getMedium()));
        }

        if (updatedCourse.getUnits() != null) {
            existing.getUnits().clear();
            existing.getUnits().addAll(updatedCourse.getUnits());
            existing.getUnits().forEach(u -> u.setCourse(existing));
        }

        Course saved = courseRepository.save(existing);
        logger.info("Updated course: {} (id={})", saved.getName(), saved.getId());
        // populate transient mediums for outgoing JSON
        saved.setMediums(mediumConverter.convertToEntityAttribute(saved.getMedium()));
        return saved;
    }

    // Patch partial update: expects keys like name, description, board, mediums (list), grade, subject
    public Course patchCourse(UUID courseId, Map<String, Object> updates) {
        Course course = getCourseById(courseId);

        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> course.setName((String) value);
                case "description" -> course.setDescription((String) value);
                case "board" -> course.setBoard((String) value);
                case "grade" -> course.setGrade((String) value);
                case "subject" -> course.setSubject((String) value);
                case "mediums" -> {
                    if (value instanceof List<?> list) {
                        List<Enums.Medium> mediums = list.stream()
                                .map(Object::toString)
                                .map(s -> {
                                    try { return Enums.Medium.valueOf(s); }
                                    catch (IllegalArgumentException e) {
                                        throw new BadRequestException("Invalid medium: " + s);
                                    }
                                })
                                .collect(Collectors.toList());
                        course.setMedium(mediumConverter.convertToDatabaseColumn(mediums));
                        course.setMediums(mediums);
                    } else {
                        throw new BadRequestException("mediums must be an array of valid medium names");
                    }
                }
            }
        });

        Course saved = courseRepository.save(course);
        saved.setMediums(mediumConverter.convertToEntityAttribute(saved.getMedium()));
        logger.info("Patched course: {} (id={})", saved.getName(), saved.getId());
        return saved;
    }

//    // Delete
//    public void deleteCourse(UUID courseId) {
//        if (!courseRepository.existsById(courseId)) throw new NotFoundException("Course not found with id: " + courseId);
//        courseRepository.deleteById(courseId);
//        logger.info("Deleted course (id={})", courseId);
//    }
//    public void deleteCourse(UUID courseId) {
//        Course course = courseRepository.findById(courseId)
//                .orElseThrow(() -> new NotFoundException("Course not found"));
//
//        // Nullify course reference in units
//        course.getUnits().forEach(unit -> unit.setCourse(null));
//
//        courseRepository.delete(course);
//    }
    public void deleteCourse(UUID courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found"));

        // Detach units first
        List<Unit> units = course.getUnits();
        if (units != null) {
            for (Unit unit : units) {
                unit.setCourse(null); // set course_id to NULL in DB
            }
            unitRepository.saveAll(units); // save units with null course_id
        }

        // Now delete the course
        courseRepository.delete(course);
    }



    /**
     * Pagination + filter + search + sort
     * - This method uses repository pageable to fetch pages (so DB-level paging).
     * - Then it applies in-memory filtering/search on that page content, and returns a PageImpl.
     *   (This avoids loading the entire DB at once, but for heavy filtering across whole DB use Specifications.)
     */
    public Page<Course> filterSearchSortPageable(String board, String grade, String subject,
                                                 String search, String orderBy, String direction,
                                                 Pageable pageable) {

        Page<Course> page = courseRepository.findAll(pageable);

        List<Course> filtered = page.getContent().stream()
                .filter(c -> board == null || board.isBlank() || c.getBoard().equalsIgnoreCase(board))
                .filter(c -> grade == null || grade.isBlank() || c.getGrade().equalsIgnoreCase(grade))
                .filter(c -> subject == null || subject.isBlank() || c.getSubject().equalsIgnoreCase(subject))
                .filter(c -> {
                    if (search == null || search.isBlank()) return true;
                    String s = search.toLowerCase();
                    return (c.getName() != null && c.getName().toLowerCase().contains(s))
                            || (c.getDescription() != null && c.getDescription().toLowerCase().contains(s));
                })
                .collect(Collectors.toList());

        // Sorting is typically done by Pageable; if extra sorting required, you can sort filtered list here.

        // Ensure transient mediums are populated before returning
        filtered.forEach(c -> c.setMediums(mediumConverter.convertToEntityAttribute(c.getMedium())));

        return new org.springframework.data.domain.PageImpl<>(filtered, pageable, page.getTotalElements());
    }
}
