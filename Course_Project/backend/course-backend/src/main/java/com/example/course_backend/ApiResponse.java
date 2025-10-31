package com.example.course_backend;

import lombok.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private String id;
    private String ver = "v1";
    private Instant ts;
    private Params params;
    private String responseCode;
    private T result;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Params {
        private String msgid;
        private String status;
        private String err;
        private String errmsg;
    }
}
