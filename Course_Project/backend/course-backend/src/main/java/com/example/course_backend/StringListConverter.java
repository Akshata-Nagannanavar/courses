//package com.example.course_backend;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import jakarta.persistence.AttributeConverter;
//import jakarta.persistence.Converter;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Converter
//public class StringListConverter implements AttributeConverter<List<String>, String> {
//
//    private static final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Override
//    public String convertToDatabaseColumn(List<String> list) {
//        try {
//            if (list == null || list.isEmpty()) {
//                return "[]";
//            }
//            return objectMapper.writeValueAsString(list);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to convert List<String> to JSON", e);
//        }
//    }
//
//    @Override
//    public List<String> convertToEntityAttribute(String dbData) {
//        try {
//            if (dbData == null || dbData.isBlank()) {
//                return new ArrayList<>();
//            }
//
//            dbData = dbData.trim();
//
//            // ✅ If DB value is not JSON (like "CBSE"), wrap it as a single-item list
//            if (!dbData.startsWith("[") && !dbData.endsWith("]")) {
//                List<String> single = new ArrayList<>();
//                single.add(dbData);
//                return single;
//            }
//
//            return objectMapper.readValue(dbData, new TypeReference<List<String>>() {});
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to convert JSON string to List<String>: " + dbData, e);
//        }
//    }
//}
package com.example.course_backend;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    @Override
    public String convertToDatabaseColumn(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        // Store as comma-separated string
        return String.join(",", list);
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Collections.emptyList();
        }
        // Convert comma-separated string back to List
        return Arrays.stream(dbData.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }
}
