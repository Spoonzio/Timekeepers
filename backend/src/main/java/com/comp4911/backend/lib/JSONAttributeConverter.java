package com.comp4911.backend.lib;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Converter
public class JSONAttributeConverter implements AttributeConverter<Map<String, String>, String> {
    @Override
    public String convertToDatabaseColumn(Map<String, String> attribute) {
        if (attribute == null || attribute.isEmpty()) {
            return "";
        }
        return attribute.toString();
    }

    @Override
    public Map<String, String> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return new HashMap<>();
        }
        dbData = dbData.replace("{", "").replace("}", "");
        Map<String, String> map = Arrays.stream(dbData.split(","))
                .map(entry -> entry.split("="))
                .collect(Collectors.toMap(entry -> entry[0].trim(), entry -> entry[1].trim()));
        return map;
    }
}
