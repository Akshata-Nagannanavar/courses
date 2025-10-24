package com.example.course_backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.*;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> createCourse(@RequestBody Course course) {
        logger.info("Create course request: {}", course);
        Course created = courseService.createCourse(course);
        Map<String, Object> result = Map.of("message", "Course created successfully", "data", created);
        return ResponseEntity.status(201).body(ResponseUtil.successWithData("api.course.create", result));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> filterSearchSortPageable(
            @RequestParam(required = false) String board,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String orderBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Pageable pageable = PageRequest.of(page, size,
                direction.equalsIgnoreCase("asc") ? Sort.by(orderBy).ascending() : Sort.by(orderBy).descending());

        Page<Course> coursesPage = courseService.filterSearchSortPageable(board, grade, subject, search, orderBy, direction, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("message", coursesPage.isEmpty() ? "No courses found" : "Courses fetched successfully");
        result.put("data", coursesPage.getContent());
        result.put("totalPages", coursesPage.getTotalPages());
        result.put("totalElements", coursesPage.getTotalElements());
        result.put("currentPage", coursesPage.getNumber());

        return ResponseEntity.ok(ResponseUtil.successWithData("api.course.getAll", result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCourseById(@PathVariable UUID id) {
        Course course = courseService.getCourseById(id);
        Map<String, Object> result = Map.of("message", "Course fetched successfully", "data", course);
        return ResponseEntity.ok(ResponseUtil.successWithData("api.course.getById", result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateCourse(@PathVariable UUID id, @RequestBody Course updatedCourse) {
        Course course = courseService.updateCourse(id, updatedCourse);
        Map<String, Object> result = Map.of("message", "Course updated successfully", "data", course);
        return ResponseEntity.ok(ResponseUtil.successWithData("api.course.update", result));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> patchCourse(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {
        Course course = courseService.patchCourse(id, updates);
        Map<String, Object> result = Map.of("message", "Course partially updated successfully", "data", course);
        return ResponseEntity.ok(ResponseUtil.successWithData("api.course.patch", result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteCourse(@PathVariable UUID id) {
        courseService.deleteCourse(id);
        Map<String, Object> result = Map.of("message", "Course deleted successfully");
        return ResponseEntity.ok(ResponseUtil.successWithData("api.course.delete", result));
    }
}
