package com.fdg.generator;

import com.fdg.model.FieldDefinition;
import com.fdg.model.FieldType;

/**
 * Strategy interface: one implementation per FieldType.
 * The engine picks the right generator from the registry based on the
 * field's type, then calls generate() once per record.
 */
public interface FieldGenerator {

    FieldType type();

    /**
     * Produce one value for the given field. Implementations read any
     * type-specific options from field.getConfig().
     */
    Object generate(FieldDefinition field, GenerationContext ctx);
}
