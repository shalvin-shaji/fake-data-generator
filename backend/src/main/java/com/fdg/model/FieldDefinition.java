package com.fdg.model;

import java.util.HashMap;
import java.util.Map;

public class FieldDefinition {
    private String name;
    private FieldType type;
    private Map<String, Object> config = new HashMap<>();
    private double nullProbability = 0.0;

    public FieldDefinition() {}

    public FieldDefinition(String name, FieldType type, Map<String, Object> config, double nullProbability) {
        this.name = name;
        this.type = type;
        this.config = config != null ? config : new HashMap<>();
        this.nullProbability = nullProbability;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String name;
        private FieldType type;
        private Map<String, Object> config = new HashMap<>();
        private double nullProbability = 0.0;

        public Builder name(String name) { this.name = name; return this; }
        public Builder type(FieldType type) { this.type = type; return this; }
        public Builder config(Map<String, Object> config) { this.config = config; return this; }
        public Builder nullProbability(double nullProbability) { this.nullProbability = nullProbability; return this; }

        public FieldDefinition build() {
            return new FieldDefinition(name, type, config, nullProbability);
        }
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public FieldType getType() { return type; }
    public void setType(FieldType type) { this.type = type; }
    public Map<String, Object> getConfig() { return config; }
    public void setConfig(Map<String, Object> config) { this.config = config; }
    public double getNullProbability() { return nullProbability; }
    public void setNullProbability(double nullProbability) { this.nullProbability = nullProbability; }
}
