package com.fdg.generator;

import com.fdg.model.FieldType;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class FieldGeneratorRegistry {

    private static final Logger log = LoggerFactory.getLogger(FieldGeneratorRegistry.class);

    private final List<FieldGenerator> generators;
    private final Map<FieldType, FieldGenerator> byType = new EnumMap<>(FieldType.class);

    public FieldGeneratorRegistry(List<FieldGenerator> generators) {
        this.generators = generators;
    }

    @PostConstruct
    void init() {
        for (FieldGenerator g : generators) {
            FieldGenerator existing = byType.put(g.type(), g);
            if (existing != null) {
                throw new IllegalStateException(
                        "Duplicate FieldGenerator for type " + g.type() +
                        ": " + existing.getClass() + " and " + g.getClass());
            }
        }
        log.info("Registered {} field generators: {}", byType.size(), byType.keySet());

        for (FieldType t : FieldType.values()) {
            if (!byType.containsKey(t)) {
                log.warn("No FieldGenerator registered for type {}", t);
            }
        }
    }

    public FieldGenerator forType(FieldType type) {
        FieldGenerator g = byType.get(type);
        if (g == null) {
            throw new IllegalArgumentException("No generator registered for type " + type);
        }
        return g;
    }

    public boolean has(FieldType type) {
        return byType.containsKey(type);
    }

    public List<FieldGenerator> getGenerators() {
        return generators;
    }
}
