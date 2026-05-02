package com.fdg.generator.fieldtypes;

import com.fdg.generator.FieldGenerator;
import com.fdg.generator.GenerationContext;
import com.fdg.model.FieldDefinition;
import com.fdg.model.FieldType;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/** Date/time, choice, and pattern field generators. */
public final class MiscGenerators {

    /**
     * Date in ISO yyyy-MM-dd. Config:
     *   - mode: "today" | "range" (default "range")
     *   - daysBack (int, default 365)
     *   - daysForward (int, default 0)
     */
    @Component
    public static class DateGenerator implements FieldGenerator {
        @Override public FieldType type() { return FieldType.DATE; }
        @Override
        public Object generate(FieldDefinition f, GenerationContext ctx) {
            String mode = ConfigReader.getString(f.getConfig(), "mode", "range");
            if ("today".equalsIgnoreCase(mode)) {
                return LocalDate.now().toString();
            }
            int back = ConfigReader.getInt(f.getConfig(), "daysBack", 365);
            int fwd  = ConfigReader.getInt(f.getConfig(), "daysForward", 0);
            long offset = ThreadLocalRandom.current().nextLong(-back, fwd + 1);
            return LocalDate.now().plusDays(offset).format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
    }

    /**
     * ISO-8601 instant. Config:
     *   - mode: "now" | "range" (default "now")
     *   - secondsBack / secondsForward (long) for range mode
     */
    @Component
    public static class TimestampGenerator implements FieldGenerator {
        @Override public FieldType type() { return FieldType.TIMESTAMP; }
        @Override
        public Object generate(FieldDefinition f, GenerationContext ctx) {
            String mode = ConfigReader.getString(f.getConfig(), "mode", "now");
            if ("now".equalsIgnoreCase(mode)) {
                return Instant.now().toString();
            }
            long back = ConfigReader.getLong(f.getConfig(), "secondsBack", 86_400L);
            long fwd  = ConfigReader.getLong(f.getConfig(), "secondsForward", 0L);
            long offset = ThreadLocalRandom.current().nextLong(-back, fwd + 1);
            return Instant.now().plusSeconds(offset).toString();
        }
    }

    /** Uniformly picks from config.values (List<Object>). */
    @Component
    public static class EnumGenerator implements FieldGenerator {
        @Override public FieldType type() { return FieldType.ENUM; }
        @Override
        public Object generate(FieldDefinition f, GenerationContext ctx) {
            List<Object> values = ConfigReader.getList(f.getConfig(), "values");
            if (values.isEmpty()) {
                throw new IllegalStateException(
                        "ENUM field '" + f.getName() + "' requires a non-empty 'values' list");
            }
            return values.get(ThreadLocalRandom.current().nextInt(values.size()));
        }
    }

    /**
     * Weighted enum. Config:
     *   - values: List<Map<String,Object>> with keys "value" and "weight" (number).
     * Example: [{value:"BUY", weight:0.7}, {value:"SELL", weight:0.3}]
     */
    @Component
    public static class WeightedEnumGenerator implements FieldGenerator {
        @Override public FieldType type() { return FieldType.WEIGHTED_ENUM; }
        @Override
        public Object generate(FieldDefinition f, GenerationContext ctx) {
            List<Object> raw = ConfigReader.getList(f.getConfig(), "values");
            if (raw.isEmpty()) {
                throw new IllegalStateException(
                        "WEIGHTED_ENUM field '" + f.getName() + "' requires 'values'");
            }
            double total = 0;
            for (Object o : raw) {
                Map<?, ?> m = (Map<?, ?>) o;
                total += ((Number) m.get("weight")).doubleValue();
            }
            double pick = ThreadLocalRandom.current().nextDouble(total);
            double cum = 0;
            for (Object o : raw) {
                Map<?, ?> m = (Map<?, ?>) o;
                cum += ((Number) m.get("weight")).doubleValue();
                if (pick < cum) return m.get("value");
            }
            // Fallback (floating-point edge case)
            return ((Map<?, ?>) raw.get(raw.size() - 1)).get("value");
        }
    }

    /** Regex-based generator. Config: pattern (string, required). */
    @Component
    public static class RegexGenerator implements FieldGenerator {
        @Override public FieldType type() { return FieldType.REGEX; }
        @Override
        public Object generate(FieldDefinition f, GenerationContext ctx) {
            String pattern = ConfigReader.getString(f.getConfig(), "pattern", null);
            if (pattern == null) {
                throw new IllegalStateException(
                        "REGEX field '" + f.getName() + "' requires a 'pattern'");
            }
            return ctx.getFaker().regexify(pattern);
        }
    }
}
