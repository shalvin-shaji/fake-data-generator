package com.fdg.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ModelDefinition {
    private String id;
    private String name;
    private String kafkaTopic;
    private String keyField;
    private List<FieldDefinition> fields = new ArrayList<>();

    public ModelDefinition() {}

    public ModelDefinition(String id, String name, String kafkaTopic, String keyField, List<FieldDefinition> fields) {
        this.id = id;
        this.name = name;
        this.kafkaTopic = kafkaTopic;
        this.keyField = keyField;
        this.fields = fields != null ? fields : new ArrayList<>();
    }

    public static ModelDefinition newId() {
        ModelDefinition m = new ModelDefinition();
        m.setId(UUID.randomUUID().toString());
        return m;
    }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private String id;
        private String name;
        private String kafkaTopic;
        private String keyField;
        private List<FieldDefinition> fields = new ArrayList<>();

        public Builder id(String id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder kafkaTopic(String kafkaTopic) { this.kafkaTopic = kafkaTopic; return this; }
        public Builder keyField(String keyField) { this.keyField = keyField; return this; }
        public Builder fields(List<FieldDefinition> fields) { this.fields = fields; return this; }

        public ModelDefinition build() {
            return new ModelDefinition(id, name, kafkaTopic, keyField, fields);
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getKafkaTopic() { return kafkaTopic; }
    public void setKafkaTopic(String kafkaTopic) { this.kafkaTopic = kafkaTopic; }
    public String getKeyField() { return keyField; }
    public void setKeyField(String keyField) { this.keyField = keyField; }
    public List<FieldDefinition> getFields() { return fields; }
    public void setFields(List<FieldDefinition> fields) { this.fields = fields; }
}
