package com.example.course_backend;

import com.example.course_backend.Unit;
import com.example.course_backend.UnitService;
import com.example.course_backend.ApiResponse;
import com.example.course_backend.ResponseUtil;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/units")
public class UnitController {

    private final UnitService unitService;

    public UnitController(UnitService unitService) {
        this.unitService = unitService;
    }

    @PostMapping
    public ApiResponse<Map<String, String>> createUnit(@RequestBody Unit unit) {
        unitService.createUnit(unit);
        return ResponseUtil.success("api.unit.create", "Unit created successfully");
    }

    @GetMapping
    public ApiResponse<List<Unit>> getAllUnits() {
        List<Unit> units = unitService.getAllUnits();
        return ResponseUtil.successWithData("api.unit.getAll", units);
    }
}
