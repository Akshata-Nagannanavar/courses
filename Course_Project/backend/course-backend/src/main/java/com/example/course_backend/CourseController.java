package com.example.course_backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
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

    @GetMapping("/enums")
    public Map<String, List<String>> getEnums() {
        return Map.of(
                "boards", Arrays.stream(Board.values()).map(Enum::name).toList(),
                "subjects", Arrays.stream(Subject.values()).map(Enum::name).toList(),
                "mediums", Arrays.stream(Medium.values()).map(Enum::name).toList(),
                "grades", Arrays.stream(Grade.values()).map(Enum::name).toList()
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAllCourses(
            @RequestParam(required = false) String board,
            @RequestParam(required = false) String medium,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "name") String orderBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Pageable pageable = PageRequest.of(page, size,
                direction.equalsIgnoreCase("asc")
                        ? Sort.by(orderBy).ascending()
                        : Sort.by(orderBy).descending());

        Page<Course> coursesPage = courseService.filterSearchSortPageable(
                board, medium,subject, grade, search, orderBy, direction, pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("message", coursesPage.isEmpty() ? "No courses found" : "Courses fetched successfully");
        result.put("data", coursesPage.getContent());
        result.put("totalPages", coursesPage.getTotalPages());
        result.put("totalElements", coursesPage.getTotalElements());
        result.put("currentPage", coursesPage.getNumber());

        return ResponseEntity.ok(ResponseUtil.successWithData("api.course.getAll", result));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCourseById(@PathVariable Integer id) {
        Course course = courseService.getCourseById(id);
        Map<String, Object> result = Map.of("message", "Course fetched successfully", "data", course);
        return ResponseEntity.ok(ResponseUtil.successWithData("api.course.getById", result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateCourse(@PathVariable Integer id,
                                                                         @RequestBody Course updatedCourse) {
        Course course = courseService.updateCourse(id, updatedCourse);
        Map<String, Object> result = Map.of("message", "Course updated successfully", "data", course);
        return ResponseEntity.ok(ResponseUtil.successWithData("api.course.update", result));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> patchCourse(@PathVariable Integer id,
                                                                        @RequestBody Map<String, Object> updates) {
        Course course = courseService.patchCourse(id, updates);
        Map<String, Object> result = Map.of("message", "Course partially updated successfully", "data", course);
        return ResponseEntity.ok(ResponseUtil.successWithData("api.course.patch", result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteCourse(@PathVariable Integer id) {
        courseService.deleteCourse(id);
        Map<String, Object> result = Map.of("message", "Course deleted successfully");
        return ResponseEntity.ok(ResponseUtil.successWithData("api.course.delete", result));
    }
}