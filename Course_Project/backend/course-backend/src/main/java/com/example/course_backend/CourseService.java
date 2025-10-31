package com.example.course_backend;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

@Service
public class CourseService {

    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);

    private final CourseRepository courseRepository;
    private final UnitRepository unitRepository;

    public CourseService(CourseRepository courseRepository, UnitRepository unitRepository) {
        this.courseRepository = courseRepository;
        this.unitRepository = unitRepository;
    }

    @CacheEvict(value = {"courses", "coursesList"}, allEntries = true)
    public Course createCourse(Course course) {
        if (course.getName() == null || course.getName().isBlank())
            throw new BadRequestException("Name is required");
        if (course.getDescription() == null || course.getDescription().isBlank())
            throw new BadRequestException("Description is required");
        if (course.getSubject() == null || course.getSubject().isEmpty())
            throw new BadRequestException("At least one subject is required");
        if (course.getBoard() == null || course.getBoard().isBlank())
            throw new BadRequestException("Board is required");
        if (course.getMedium() == null || course.getMedium().isEmpty())
            throw new BadRequestException("At least one medium is required");
        if (course.getGrade() == null || course.getGrade().isEmpty())
            throw new BadRequestException("At least one grade is required");

        if (course.getUnits() == null) course.setUnits(new ArrayList<>());

        Course saved = courseRepository.save(course);
        logger.info("Created course: {} (id={})", course.getName(), saved.getId());
        return saved;
    }

    @Cacheable(value = "courses", key = "#courseId")
    public Course getCourseById(UUID courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found with id: " + courseId));
    }

    @CacheEvict(value = {"courses", "coursesList"}, allEntries = true)
    public Course updateCourse(UUID courseId, Course updatedCourse) {
        Course existing = getCourseById(courseId);

        if (updatedCourse.getName() != null && !updatedCourse.getName().isBlank())
            existing.setName(updatedCourse.getName());
        if (updatedCourse.getDescription() != null && !updatedCourse.getDescription().isBlank())
            existing.setDescription(updatedCourse.getDescription());
        if (updatedCourse.getSubject() != null && !updatedCourse.getSubject().isEmpty())
            existing.setSubject(updatedCourse.getSubject());
        if (updatedCourse.getBoard() != null && !updatedCourse.getBoard().isBlank())
            existing.setBoard(updatedCourse.getBoard());
        if (updatedCourse.getMedium() != null && !updatedCourse.getMedium().isEmpty())
            existing.setMedium(updatedCourse.getMedium());
        if (updatedCourse.getGrade() != null && !updatedCourse.getGrade().isEmpty())
            existing.setGrade(updatedCourse.getGrade());

        if (updatedCourse.getUnits() != null) {
            existing.getUnits().clear();
            existing.getUnits().addAll(updatedCourse.getUnits());
            existing.getUnits().forEach(u -> u.setCourse(existing));
        }

        Course saved = courseRepository.save(existing);
        logger.info("Updated course: {} (id={})", saved.getName(), saved.getId());
        return saved;
    }

    @CacheEvict(value = {"courses", "coursesList"}, allEntries = true)
    public Course patchCourse(UUID courseId, Map<String, Object> updates) {
        Course course = getCourseById(courseId);

        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> course.setName((String) value);
                case "description" -> course.setDescription((String) value);
                case "subject" -> {
                    if (value instanceof List<?> list)
                        course.setSubject(list.stream().map(Object::toString).toList());
                }
                case "board" -> course.setBoard((String) value);
                case "medium" -> {
                    if (value instanceof List<?> list)
                        course.setMedium(list.stream().map(Object::toString).toList());
                }
                case "grade" -> {
                    if (value instanceof List<?> list)
                        course.setGrade(list.stream().map(Object::toString).toList());
                }
            }
        });

        Course saved = courseRepository.save(course);
        logger.info("Patched course: {} (id={})", saved.getName(), saved.getId());
        return saved;
    }

    @CacheEvict(value = {"courses", "coursesList"}, allEntries = true)
    public void deleteCourse(UUID courseId) {
        Course course = getCourseById(courseId);

        if (course.getUnits() != null) {
            for (Unit unit : course.getUnits()) {
                unit.setCourse(null);
            }
            unitRepository.saveAll(course.getUnits());
        }

        courseRepository.delete(course);
        logger.info("Deleted course: {} (id={})", course.getName(), course.getId());
    }

    @Cacheable(value = "coursesList", key = "#root.methodName + '_' + #board + '_' + #subject + '_' + #grade + '_' + #search + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<Course> filterSearchSortPageable(String board, String medium,String subject, String grade,
                                                 String search, String orderBy, String direction,
                                                 Pageable pageable) {

        List<Course> allCourses = courseRepository.findAll();

        List<Course> filtered = allCourses.stream()
                .filter(c -> board == null || board.isBlank() || (c.getBoard() != null && c.getBoard().equalsIgnoreCase(board)))
                .filter(c -> medium == null || medium.isBlank() || (c.getMedium() != null && c.getMedium().contains(medium)))
                .filter(c -> subject == null || subject.isBlank() || (c.getSubject() != null && c.getSubject().contains(subject)))
                .filter(c -> grade == null || grade.isBlank() || (c.getGrade() != null && c.getGrade().contains(grade)))
                .filter(c -> {
                    if (search == null || search.isBlank()) return true;
                    String s = search.toLowerCase();
                    return (c.getName() != null && c.getName().toLowerCase().contains(s))
                            || (c.getDescription() != null && c.getDescription().toLowerCase().contains(s));
                })
                .collect(Collectors.toList());

        Comparator<Course> comparator = Comparator.comparing(Course::getName, String.CASE_INSENSITIVE_ORDER);
        if ("desc".equalsIgnoreCase(direction)) comparator = comparator.reversed();
        filtered.sort(comparator);

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        List<Course> pageContent = start >= filtered.size() ? List.of() : filtered.subList(start, end);

        return new PageImpl<>(pageContent, pageable, filtered.size());
    }
}
