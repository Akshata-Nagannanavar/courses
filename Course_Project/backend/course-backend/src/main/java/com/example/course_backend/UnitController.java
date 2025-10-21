

package com.example.course_backend;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/courses/{courseId}/units")
public class UnitController {

    private final UnitService unitService;

    public UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> addUnitToCourse(
            @PathVariable UUID courseId,
            @RequestBody Unit unit
    ) {
        unitService.addUnitToCourse(courseId, unit);
        Map<String, Object> result = Map.of(
                "message", "Unit added successfully",
                "data", unit
        );
        return ResponseUtil.successWithData("api.unit.create", result);
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> getUnitsByCourse(@PathVariable UUID courseId) {
        List<Unit> units = unitService.getUnitsByCourseId(courseId);
        Map<String, Object> result = Map.of(
                "message", "Units fetched successfully",
                "data", units
        );
        return ResponseUtil.successWithData("api.unit.getAll", result);
    }
}

