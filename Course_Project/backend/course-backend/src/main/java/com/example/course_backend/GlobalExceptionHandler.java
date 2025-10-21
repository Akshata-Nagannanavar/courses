package com.example.course_backend;

import com.example.course_backend.ApiResponse;
import com.example.course_backend.ResponseUtil;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleException(Exception ex) {
        return ResponseUtil.failure("api.error", "500", ex.getMessage());
    }
}
