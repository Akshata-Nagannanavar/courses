//
//
//package com.example.course_backend;
//
//import org.springframework.web.bind.annotation.*;
//import java.util.*;
//
//@RestController
//@RequestMapping("/api/courses/{courseId}/units")
//public class UnitController {
//
//    private final UnitService unitService;
//
//    public UnitController(UnitService unitService) {
//        this.unitService = unitService;
//    }
//
//    @PostMapping
//    public ApiResponse<Map<String, Object>> addUnitToCourse(
//            @PathVariable UUID courseId,
//            @RequestBody Unit unit
//    ) {
//        unitService.addUnitToCourse(courseId, unit);
//        Map<String, Object> result = Map.of(
//                "message", "Unit added successfully",
//                "data", unit
//        );
//        return ResponseUtil.successWithData("api.unit.create", result);
//    }
//
//    @GetMapping
//    public ApiResponse<Map<String, Object>> getUnitsByCourse(@PathVariable UUID courseId) {
//        List<Unit> units = unitService.getUnitsByCourseId(courseId);
//        Map<String, Object> result = Map.of(
//                "message", "Units fetched successfully",
//                "data", units
//        );
//        return ResponseUtil.successWithData("api.unit.getAll", result);
//    }
//}
//

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

    @PutMapping("/{unitId}")
    public ApiResponse<Map<String, Object>> updateUnit(
            @PathVariable UUID courseId,
            @PathVariable UUID unitId,
            @RequestBody Unit updatedUnit
    ) {
        Unit unit = unitService.updateUnit(courseId, unitId, updatedUnit);
        Map<String, Object> result = Map.of(
                "message", "Unit updated successfully",
                "data", unit
        );
        return ResponseUtil.successWithData("api.unit.update", result);
    }

    @PatchMapping("/{unitId}")
    public ApiResponse<Map<String, Object>> patchUnit(
            @PathVariable UUID courseId,
            @PathVariable UUID unitId,
            @RequestBody Map<String, Object> updates
    ) {
        Unit unit = unitService.patchUnit(courseId, unitId, updates);
        Map<String, Object> result = Map.of(
                "message", "Unit patched successfully",
                "data", unit
        );
        return ResponseUtil.successWithData("api.unit.patch", result);
    }

    @DeleteMapping("/{unitId}")
    public ApiResponse<Map<String, String>> deleteUnit(
            @PathVariable UUID courseId,
            @PathVariable UUID unitId
    ) {
        unitService.deleteUnit(courseId, unitId);
        return ResponseUtil.success("api.unit.delete", "Unit deleted successfully");
    }
}
