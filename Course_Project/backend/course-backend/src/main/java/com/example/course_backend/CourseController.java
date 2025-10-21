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

//    @GetMapping
//    public ApiResponse<List<Course>> getAllCourses() {
//        List<Course> courses = courseService.getAllCourses();
//        return ResponseUtil.successWithData("api.course.getAll", courses);
//    }
    @GetMapping
    public ApiResponse<Map<String, Object>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        Map<String, Object> result = Map.of(
                "message", "Courses fetched successfully",
                "data", courses
        );
        return ResponseUtil.successWithData("api.course.getAll", result);
    }

}
