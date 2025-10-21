package com.example.course_backend;

import lombok.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private String id;           // e.g., "api.course.create"
    private String ver = "v1";   // version
    private Instant ts;          // timestamp
    private Params params;       // metadata
    private String responseCode; // "OK" or "ERROR"
    private T result;            // actual data

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Params {
        private String msgid;     // unique UUID for request
        private String status;    // "success" or "failure"
        private String err;       // error code
        private String errmsg;    // error message
    }
}
