package com.example.course_backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import java.util.*;

@RestController
@RequestMapping("/api/courses/{courseId}/units")
public class UnitController {

    private final UnitService unitService;

    public UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> addUnitToCourse(
            @PathVariable UUID courseId,
            @RequestBody Unit unit) {
        Unit created = unitService.addUnitToCourse(courseId, unit);
        return ResponseEntity.status(201).body(ResponseUtil.successWithData("api.unit.create",
                Map.of("message", "Unit added successfully", "data", created)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUnitsByCourse(
            @PathVariable UUID courseId,
            @PageableDefault(size = 10) Pageable pageable) {
        var page = unitService.getUnitsByCoursePaginated(courseId, pageable);
        Map<String, Object> result = new HashMap<>();
        result.put("message", page.isEmpty() ? "No units found" : "Units fetched successfully");
        result.put("data", page.getContent());
        result.put("totalPages", page.getTotalPages());
        result.put("totalElements", page.getTotalElements());
        result.put("currentPage", page.getNumber());
        return ResponseEntity.ok(ResponseUtil.successWithData("api.unit.getAll", result));
    }

    @PutMapping("/{unitId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateUnit(
            @PathVariable UUID courseId,
            @PathVariable UUID unitId,
            @RequestBody Unit updatedUnit) {
        Unit saved = unitService.updateUnit(courseId, unitId, updatedUnit);
        return ResponseEntity.ok(ResponseUtil.successWithData("api.unit.update",
                Map.of("message", "Unit updated successfully", "data", saved)));
    }

    @PatchMapping("/{unitId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> patchUnit(
            @PathVariable UUID courseId,
            @PathVariable UUID unitId,
            @RequestBody Map<String, Object> updates) {
        Unit patched = unitService.patchUnit(courseId, unitId, updates);
        return ResponseEntity.ok(ResponseUtil.successWithData("api.unit.patch",
                Map.of("message", "Unit patched successfully", "data", patched)));
    }

    @DeleteMapping("/{unitId}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> deleteUnit(
            @PathVariable UUID courseId,
            @PathVariable UUID unitId) {
        unitService.deleteUnit(courseId, unitId);
        return ResponseEntity.ok(ResponseUtil.successWithData("api.unit.delete",
                Map.of("message", "Unit deleted successfully")));
    }
}
