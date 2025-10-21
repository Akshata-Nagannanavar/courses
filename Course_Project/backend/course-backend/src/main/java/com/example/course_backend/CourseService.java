
package com.example.course_backend;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.UUID;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course createCourse(Course course) {
        return courseRepository.save(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
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
                case "board" -> course.setBoard(Enums.Board.valueOf(((String) value).toUpperCase()));
                case "medium" -> course.setMedium(Enums.Medium.valueOf(((String) value).toUpperCase()));
                case "grade" -> course.setGrade(Enums.Grade.valueOf(((String) value).toUpperCase()));
                case "subject" -> course.setSubject(Enums.Subject.valueOf(((String) value).toUpperCase()));
            }
        });
        return courseRepository.save(course);
    }

    public void deleteCourse(UUID courseId) {
        courseRepository.deleteById(courseId);
    }
}
