package com.fdg.generator;

import com.fdg.model.FieldDefinition;
import com.fdg.model.ModelDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

@Service
public class ModelGeneratorEngine {

    private static final Logger log = LoggerFactory.getLogger(ModelGeneratorEngine.class);

    private final FieldGeneratorRegistry registry;

    public ModelGeneratorEngine(FieldGeneratorRegistry registry) {
        this.registry = registry;
    }

    public Map<String, Object> generateOne(ModelDefinition model, GenerationContext ctx) {
        Map<String, Object> record = new LinkedHashMap<>();
        for (FieldDefinition field : model.getFields()) {
            Object value;
            if (field.getNullProbability() > 0
                    && ThreadLocalRandom.current().nextDouble() < field.getNullProbability()) {
                value = null;
            } else {
                FieldGenerator gen = registry.forType(field.getType());
                value = gen.generate(field, ctx);
            }
            record.put(field.getName(), value);
        }

        if (model.getKeyField() != null) {
            Object key = record.get(model.getKeyField());
            if (key != null) {
                String poolKey = model.getName() + "." + model.getKeyField();
                ctx.getOrCreatePool(poolKey, 10_000).push(key);
            }
        }
        return record;
    }

    public List<Map<String, Object>> generateMany(ModelDefinition model, int count, GenerationContext ctx) {
        return IntStream.range(0, count)
                .mapToObj(i -> generateOne(model, ctx))
                .toList();
    }
}
