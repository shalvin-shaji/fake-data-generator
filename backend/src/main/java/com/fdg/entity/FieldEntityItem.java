package com.fdg.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Lightweight DTO stored inside the JSON column of ModelEntity.
 * Mirrors FieldDefinition but is decoupled from the JPA layer.
 */
public class FieldEntityItem {

    private String name;
    private String type;           // FieldType enum name stored as String
    private Map<String, Object> config = new HashMap<>();
    private double nullProbability = 0.0;

    public FieldEntityItem() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }
    public double getNullProbability() { return nullProbability; }
    public void setNullProbability(double nullProbability) { this.nullProbability = nullProbability; }
}
