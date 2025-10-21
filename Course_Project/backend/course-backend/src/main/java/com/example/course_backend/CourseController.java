

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
    public ApiResponse<Map<String, Object>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        Map<String, Object> result = Map.of(
                "message", "Courses fetched successfully",
                "data", courses
        );
        return ResponseUtil.successWithData("api.course.getAll", result);
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> getCourseById(@PathVariable UUID id) {
        Course course = courseService.getCourseById(id);
        Map<String, Object> result = Map.of(
                "message", "Course fetched successfully",
                "data", course
        );
        return ResponseUtil.successWithData("api.course.getById", result);
    }

    @PutMapping("/{id}")
    public ApiResponse<Map<String, Object>> updateCourse(
            @PathVariable UUID id,
            @RequestBody Course updatedCourse
    ) {
        Course course = courseService.updateCourse(id, updatedCourse);
        Map<String, Object> result = Map.of(
                "message", "Course updated successfully",
                "data", course
        );
        return ResponseUtil.successWithData("api.course.update", result);
    }

    @PatchMapping("/{id}")
    public ApiResponse<Map<String, Object>> patchCourse(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> updates
    ) {
        Course course = courseService.patchCourse(id, updates);
        Map<String, Object> result = Map.of(
                "message", "Course patched successfully",
                "data", course
        );
        return ResponseUtil.successWithData("api.course.patch", result);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Map<String, String>> deleteCourse(@PathVariable UUID id) {
        courseService.deleteCourse(id);
        return ResponseUtil.success("api.course.delete", "Course deleted successfully");
    }
}

