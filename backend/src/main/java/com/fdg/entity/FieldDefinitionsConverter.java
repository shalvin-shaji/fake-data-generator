package com.fdg.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.List;

/**
 * Converts List<FieldEntityItem> ↔ JSON TEXT column.
 */
@Converter
public class FieldDefinitionsConverter implements AttributeConverter<List<FieldEntityItem>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<List<FieldEntityItem>> TYPE = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(List<FieldEntityItem> fields) {
        if (fields == null || fields.isEmpty()) return "[]";
        try {
            return MAPPER.writeValueAsString(fields);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize fields to JSON", e);
        }
    }

    @Override
    public List<FieldEntityItem> convertToEntityAttribute(String json) {
        if (json == null || json.isBlank()) return new ArrayList<>();
        try {
            return MAPPER.readValue(json, TYPE);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to deserialize fields from JSON: " + json, e);
        }
    }
}
