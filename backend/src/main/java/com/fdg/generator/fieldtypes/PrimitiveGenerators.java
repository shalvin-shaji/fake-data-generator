package com.fdg.generator.fieldtypes;

import com.fdg.generator.FieldGenerator;
import com.fdg.generator.GenerationContext;
import com.fdg.model.FieldDefinition;
import com.fdg.model.FieldType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

/** Generators for primitive types. */
public final class PrimitiveGenerators {

    @Component
    public static class IntegerGenerator implements FieldGenerator {
        @Override public FieldType type() { return FieldType.INTEGER; }
        @Override
        public Object generate(FieldDefinition field, GenerationContext ctx) {
            long min = ConfigReader.getLong(field.getConfig(), "min", 0L);
            long max = ConfigReader.getLong(field.getConfig(), "max", 1_000_000L);
            return ThreadLocalRandom.current().nextLong(min, max + 1);
        }
    }

    @Component
    public static class DecimalGenerator implements FieldGenerator {
        @Override public FieldType type() { return FieldType.DECIMAL; }
        @Override
        public Object generate(FieldDefinition field, GenerationContext ctx) {
            double min = ConfigReader.getDouble(field.getConfig(), "min", 0.0);
            double max = ConfigReader.getDouble(field.getConfig(), "max", 1_000.0);
            int scale = ConfigReader.getInt(field.getConfig(), "scale", 2);
            double v = ThreadLocalRandom.current().nextDouble(min, max);
            return BigDecimal.valueOf(v).setScale(scale, RoundingMode.HALF_UP);
        }
    }

    @Component
    public static class BooleanGenerator implements FieldGenerator {
        @Override public FieldType type() { return FieldType.BOOLEAN; }
        @Override
        public Object generate(FieldDefinition field, GenerationContext ctx) {
            double trueProb = ConfigReader.getDouble(field.getConfig(), "trueProbability", 0.5);
            return ThreadLocalRandom.current().nextDouble() < trueProb;
        }
    }

    /**
     * Random text. Config:
     *   - minLength / maxLength (int)
     *   - words (boolean) — if true, produce lorem-style words instead of random chars
     */
    @Component
    public static class StringGenerator implements FieldGenerator {
        @Override public FieldType type() { return FieldType.STRING; }
        @Override
        public Object generate(FieldDefinition field, GenerationContext ctx) {
            boolean words = ConfigReader.getBoolean(field.getConfig(), "words", true);
            int minLen = ConfigReader.getInt(field.getConfig(), "minLength", 5);
            int maxLen = ConfigReader.getInt(field.getConfig(), "maxLength", 20);
            if (words) {
                int wordCount = ThreadLocalRandom.current().nextInt(1, 6);
                return ctx.getFaker().lorem().sentence(wordCount);
            }
            int len = ThreadLocalRandom.current().nextInt(minLen, maxLen + 1);
            return ctx.getFaker().lorem().characters(len);
        }
    }
}
