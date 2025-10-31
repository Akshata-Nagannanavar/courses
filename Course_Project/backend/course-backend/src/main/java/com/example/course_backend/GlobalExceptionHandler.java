
package com.example.course_backend;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 400 - Bad Request
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleBadRequest(BadRequestException ex) {
        logger.error("BadRequestException: ", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseUtil.failureWithData("api.error", "400", ex.getMessage()));
    }

    // 404 - Not Found (custom entity not found)
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleNotFound(NotFoundException ex) {
        logger.error("NotFoundException: ", ex);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseUtil.failureWithData("api.error", "404", ex.getMessage()));
    }

    // 404 - No Handler Found (invalid endpoint)
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleNoHandlerFound(NoHandlerFoundException ex) {
        logger.error("NoHandlerFoundException: ", ex);
        String message = String.format("No endpoint found for %s %s", ex.getHttpMethod(), ex.getRequestURL());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ResponseUtil.failureWithData("api.error.notfound", "404", message));
    }

    // 400 - Validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        logger.error("Validation failed: {}", errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseUtil.failureWithData("api.error.validation", "400", errors.toString()));
    }

    // 500 - Other unhandled exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleException(Exception ex) {
        logger.error("Unhandled exception: ", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseUtil.failureWithData("api.error", "500", "An unexpected error occurred"));
    }
}
