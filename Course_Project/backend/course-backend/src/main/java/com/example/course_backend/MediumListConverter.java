//package com.example.course_backend;
//
//import jakarta.persistence.AttributeConverter;
//import jakarta.persistence.Converter;
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.Stream;
//
//@Converter
//public class MediumListConverter implements AttributeConverter<List<Enums.Medium>, String> {
//
//    private static final String SPLIT_CHAR = ",";
//
//    @Override
//    public String convertToDatabaseColumn(List<Enums.Medium> attribute) {
//        if (attribute == null || attribute.isEmpty()) return "";
//        return attribute.stream()
//                .map(Enum::name)
//                .collect(Collectors.joining(SPLIT_CHAR));
//    }
//
//    @Override
//    public List<Enums.Medium> convertToEntityAttribute(String dbData) {
//        if (dbData == null || dbData.isBlank()) return new ArrayList<>();
//        return Stream.of(dbData.split(SPLIT_CHAR))
//                .map(String::trim)
//                .map(Enums.Medium::valueOf)
//                .collect(Collectors.toList());
//    }
//}

package com.example.course_backend;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Converter
public class MediumListConverter implements AttributeConverter<List<Enums.Medium>, String> {

    private static final String SPLIT_CHAR = ",";

    @Override
    public String convertToDatabaseColumn(List<Enums.Medium> attribute) {
        if (attribute == null || attribute.isEmpty()) return "";
        return attribute.stream().map(Enum::name).collect(Collectors.joining(SPLIT_CHAR));
    }

    @Override
    public List<Enums.Medium> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return List.of();
        return Arrays.stream(dbData.split(SPLIT_CHAR))
                .map(String::trim)
                .map(Enums.Medium::valueOf)
                .collect(Collectors.toList());
    }
}
