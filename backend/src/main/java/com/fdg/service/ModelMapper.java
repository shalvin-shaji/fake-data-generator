package com.fdg.service;

import com.fdg.entity.FieldEntityItem;
import com.fdg.entity.ModelEntity;
import com.fdg.model.FieldDefinition;
import com.fdg.model.FieldType;
import com.fdg.model.ModelDefinition;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Converts between the JPA entity layer and the engine POJO layer.
 */
@Component
public class ModelMapper {

    public ModelDefinition toModel(ModelEntity entity) {
        List<FieldDefinition> fields = entity.getFields().stream()
                .map(this::toFieldDefinition)
                .toList();

        return ModelDefinition.builder()
                .id(entity.getId())
                .name(entity.getName())
                .kafkaTopic(entity.getKafkaTopic())
                .keyField(entity.getKeyField())
                .fields(fields)
                .build();
    }

    public ModelEntity toEntity(ModelDefinition model) {
        ModelEntity entity = new ModelEntity();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setKafkaTopic(model.getKafkaTopic());
        entity.setKeyField(model.getKeyField());
        entity.setFields(model.getFields().stream().map(this::toFieldItem).toList());
        return entity;
    }

    private FieldDefinition toFieldDefinition(FieldEntityItem item) {
        return FieldDefinition.builder()
                .name(item.getName())
                .type(FieldType.valueOf(item.getType()))
                .config(item.getConfig())
                .nullProbability(item.getNullProbability())
                .build();
    }

    private FieldEntityItem toFieldItem(FieldDefinition field) {
        FieldEntityItem item = new FieldEntityItem();
        item.setName(field.getName());
        item.setType(field.getType().name());
        item.setConfig(field.getConfig());
        item.setNullProbability(field.getNullProbability());
        return item;
    }
}
