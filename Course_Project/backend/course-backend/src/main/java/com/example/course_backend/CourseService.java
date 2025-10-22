
package com.example.course_backend;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course createCourse(Course course) {
        // Minimal validation: check if any required field is null or blank
        if (course.getName() == null || course.getName().isBlank() ||
                course.getDescription() == null || course.getDescription().isBlank() ||
                course.getBoard() == null ||
                course.getMedium() == null ||
                course.getGrade() == null ||
                course.getSubject() == null) {
            throw new RuntimeException("All fields are required (name,description,board,medium,grade,subject) ");
        }

        //  ensure units list is not null
        if (course.getUnits() == null) {
            course.setUnits(new ArrayList<>());
        }

        return courseRepository.save(course);
    }


    public Course getCourseById(UUID courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
    }

    public Course updateCourse(UUID courseId, Course updatedCourse) {
        Course existingCourse = getCourseById(courseId);
        existingCourse.setName(updatedCourse.getName());
        existingCourse.setDescription(updatedCourse.getDescription());
        existingCourse.setBoard(updatedCourse.getBoard());
        existingCourse.setMedium(updatedCourse.getMedium());
        existingCourse.setGrade(updatedCourse.getGrade());
        existingCourse.setSubject(updatedCourse.getSubject());
        if (updatedCourse.getUnits() != null) {
            existingCourse.getUnits().clear();
            existingCourse.getUnits().addAll(updatedCourse.getUnits());
            existingCourse.getUnits().forEach(u -> u.setCourse(existingCourse));
        }
        return courseRepository.save(existingCourse);
    }

    public Course patchCourse(UUID courseId, Map<String, Object> updates) {
        Course course = getCourseById(courseId);
        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> course.setName((String) value);
                case "description" -> course.setDescription((String) value);
                case "board" -> course.setBoard(parseEnumIgnoreCase(Enums.Board.class, (String) value));
                case "medium" -> course.setMedium(parseEnumIgnoreCase(Enums.Medium.class, (String) value));
                case "grade" -> course.setGrade(parseEnumIgnoreCase(Enums.Grade.class, (String) value));
                case "subject" -> course.setSubject(parseEnumIgnoreCase(Enums.Subject.class, (String) value));
            }
        });
        return courseRepository.save(course);
    }

    public void deleteCourse(UUID courseId) {
        courseRepository.deleteById(courseId);
    }

    // Filter + search + orderBy method
    public List<Course> filterSearchSort(String board, String medium, String grade, String subject,
                                         String search, String orderBy, String direction) {

        List<Course> courses = courseRepository.findAll();

        // Filter by enum fields
        courses = courses.stream()
                .filter(c -> board == null || c.getBoard().name().equalsIgnoreCase(board))
                .filter(c -> medium == null || c.getMedium().name().equalsIgnoreCase(medium))
                .filter(c -> grade == null || c.getGrade().name().equalsIgnoreCase(grade))
                .filter(c -> subject == null || c.getSubject().name().equalsIgnoreCase(subject))
                .collect(Collectors.toList());

        // Search by name or description (case-insensitive)
        if (search != null && !search.isBlank()) {
            String searchLower = search.toLowerCase();
            courses = courses.stream()
                    .filter(c -> c.getName().toLowerCase().contains(searchLower) ||
                            c.getDescription().toLowerCase().contains(searchLower))
                    .collect(Collectors.toList());
        }

        // Order by field
        if (orderBy != null && !orderBy.isBlank()) {
            Comparator<Course> comparator;

            switch (orderBy.toLowerCase()) {
                case "name" -> comparator = Comparator.comparing(c -> c.getName().toLowerCase());
                case "grade" -> comparator = Comparator.comparing(c -> c.getGrade().name().toLowerCase());
                case "board" -> comparator = Comparator.comparing(c -> c.getBoard().name().toLowerCase());
                case "medium" -> comparator = Comparator.comparing(c -> c.getMedium().name().toLowerCase());
                case "subject" -> comparator = Comparator.comparing(c -> c.getSubject().name().toLowerCase());
                default -> comparator = Comparator.comparing(c -> c.getName().toLowerCase());
            }

            if ("desc".equalsIgnoreCase(direction)) {
                comparator = comparator.reversed();
            }
            courses.sort(comparator);
        }

        return courses;
    }

    // Helper: parse enum ignoring case
    private <T extends Enum<T>> T parseEnumIgnoreCase(Class<T> enumClass, String value) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid value '" + value + "' for enum " + enumClass.getSimpleName()
                ));
    }

}

