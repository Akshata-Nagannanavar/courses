

package com.example.course_backend;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public ApiResponse<Map<String, String>> createCourse(@RequestBody Course course) {
        courseService.createCourse(course);
        return ResponseUtil.success("api.course.create", "Course created successfully");
    }


    @GetMapping
    public ApiResponse<Map<String, Object>> getAllCourses(
            @RequestParam(required = false) String board,
            @RequestParam(required = false) String medium,
            @RequestParam(required = false) String grade,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String orderBy,
            @RequestParam(required = false) String direction
    ) {
        List<Course> courses = courseService.filterSearchSort(board, medium, grade, subject, search, orderBy, direction);

        Map<String, Object> result = new HashMap<>();

        if (courses.isEmpty()) {
            result.put("message", "No courses found");
            result.put("data", Collections.emptyList());
            return ResponseUtil.successWithData("api.course.getAll", result);
        } else {
            result.put("message", "Courses fetched successfully");
            result.put("data", courses);
            return ResponseUtil.successWithData("api.course.getAll", result);
        }
    }


    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getCourseById(@PathVariable UUID id) {
        Course course = courseService.getCourseById(id);
        if (course != null) {
            Map<String, Object> result = Map.of(
                    "message", "Course fetched successfully",
                    "data", course
            );
            return ResponseUtil.successWithData("api.course.getById", result);
        } else {
            return ResponseUtil.failureWithData("api.course.getById", "404", "Course not found");
        }
    }

    @PutMapping("/{id}")
    public ApiResponse<Map<String, Object>> updateCourse(@PathVariable UUID id, @RequestBody Course updatedCourse) {
        Course course = courseService.updateCourse(id, updatedCourse);
        Map<String, Object> result = Map.of(
                "message", "Course updated successfully",
                "data", course
        );
        return ResponseUtil.successWithData("api.course.update", result);
    }

    @PatchMapping("/{id}")
    public ApiResponse<Map<String, Object>> patchCourse(@PathVariable UUID id, @RequestBody Map<String, Object> updates) {
        Course course = courseService.patchCourse(id, updates);
        Map<String, Object> result = Map.of(
                "message", "Course partially updated successfully",
                "data", course
        );
        return ResponseUtil.successWithData("api.course.patch", result);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Map<String, Object>> deleteCourse(@PathVariable UUID id) {
        courseService.deleteCourse(id);
        Map<String, Object> result = Map.of(
                "message", "Course deleted successfully"
        );
        return ResponseUtil.successWithData("api.course.delete", result);
    }
}


