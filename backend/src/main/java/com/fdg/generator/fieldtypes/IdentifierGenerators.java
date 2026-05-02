package com.fdg.generator.fieldtypes;

import com.fdg.generator.FieldGenerator;
import com.fdg.generator.GenerationContext;
import com.fdg.generator.ReferencePool;
import com.fdg.model.FieldDefinition;
import com.fdg.model.FieldType;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** Identifier-style field generators. */
public final class IdentifierGenerators {

    @Component
    public static class UuidGenerator implements FieldGenerator {
        @Override public FieldType type() { return FieldType.UUID; }
        @Override
        public Object generate(FieldDefinition field, GenerationContext ctx) {
            return UUID.randomUUID().toString();
        }
    }

    /**
     * Monotonic per-field counter. Config:
     *   - start (long, default 1)
     *   - prefix (string, optional) — if set, returns "prefix-N"
     */
    @Component
    public static class SequenceGenerator implements FieldGenerator {
        @Override public FieldType type() { return FieldType.SEQUENCE; }
        @Override
        public Object generate(FieldDefinition field, GenerationContext ctx) {
            long start = ConfigReader.getLong(field.getConfig(), "start", 1L);
            String prefix = ConfigReader.getString(field.getConfig(), "prefix", null);
            // Note: we key sequences by field name only here; the engine can
            // namespace this further with the model id when it calls us.
            long n = ctx.nextSequence("seq." + field.getName(), start);
            return prefix == null ? n : prefix + n;
        }
    }

    /**
     * Picks a value from another model's reference pool. Config:
     *   - modelName (string, required) — name of the parent model
     *   - field    (string, required) — field whose values to reference
     *   - poolSize (int, default 10000) — capacity of the parent pool
     * Returns null if the parent pool is still empty (warm-up). The engine
     * decides how to handle that.
     */
    @Component
    public static class ReferenceGenerator implements FieldGenerator {
        @Override public FieldType type() { return FieldType.REFERENCE; }
        @Override
        public Object generate(FieldDefinition field, GenerationContext ctx) {
            String modelName = ConfigReader.getString(field.getConfig(), "modelName", null);
            String parentField = ConfigReader.getString(field.getConfig(), "field", null);
            int poolSize = ConfigReader.getInt(field.getConfig(), "poolSize", 10_000);

            if (modelName == null || parentField == null) {
                throw new IllegalStateException(
                        "REFERENCE field '" + field.getName() +
                        "' requires 'modelName' and 'field' in config");
            }
            String key = modelName + "." + parentField;
            ReferencePool pool = ctx.getOrCreatePool(key, poolSize);
            return pool.pickRandom();
        }
    }
}
