package com.example.course_backend;

import com.example.course_backend.ApiResponse;
import java.time.Instant;
import java.util.UUID;
import java.util.Map;

public class ResponseUtil {

    public static ApiResponse<Map<String, String>> success(String apiId, String message) {
        ApiResponse<Map<String, String>> response = new ApiResponse<>();
        response.setId(apiId);
        response.setVer("v1");
        response.setTs(Instant.now());
        response.setParams(new ApiResponse.Params(UUID.randomUUID().toString(), "success", null, null));
        response.setResponseCode("OK");
        response.setResult(Map.of("message", message));
        return response;
    }

    public static <T> ApiResponse<T> successWithData(String apiId, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setId(apiId);
        response.setVer("v1");
        response.setTs(Instant.now());
        response.setParams(new ApiResponse.Params(UUID.randomUUID().toString(), "success", null, null));
        response.setResponseCode("OK");
        response.setResult(data);
        return response;
    }

    public static ApiResponse<String> failure(String apiId, String errCode, String errMsg) {
        ApiResponse<String> response = new ApiResponse<>();
        response.setId(apiId);
        response.setVer("v1");
        response.setTs(Instant.now());
        response.setParams(new ApiResponse.Params(UUID.randomUUID().toString(), "failure", errCode, errMsg));
        response.setResponseCode("ERROR");
        response.setResult(null);
        return response;
    }
}
