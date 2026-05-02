package com.fdg.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity that persists a user-defined model definition.
 * The fields list is stored as a single JSON column to keep the schema
 * flexible — no join table required.
 */
@Entity
@Table(name = "model_definitions")
public class ModelEntity {

    @Id
    @Column(nullable = false, length = 36)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(name = "kafka_topic")
    private String kafkaTopic;

    @Column(name = "key_field")
    private String keyField;

    /** Serialized list of FieldDefinition POJOs as JSON. */
    @Column(name = "fields_json", nullable = false, columnDefinition = "TEXT")
    @Convert(converter = FieldDefinitionsConverter.class)
    private List<FieldEntityItem> fields = new ArrayList<>();

    public ModelEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getKafkaTopic() { return kafkaTopic; }
    public void setKafkaTopic(String kafkaTopic) { this.kafkaTopic = kafkaTopic; }
    public String getKeyField() { return keyField; }
    public void setKeyField(String keyField) { this.keyField = keyField; }
    public List<FieldEntityItem> getFields() { return fields; }
    public void setFields(List<FieldEntityItem> fields) { this.fields = fields; }
}
