package com.fdg.generator.fieldtypes;

import com.fdg.generator.FieldGenerator;
import com.fdg.generator.GenerationContext;
import com.fdg.model.FieldDefinition;
import com.fdg.model.FieldType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/** Personal-info field generators (delegating to Datafaker). */
public final class PersonalGenerators {

    @Component
    public static class FirstNameGenerator implements FieldGenerator {
        @Override public FieldType type() { return FieldType.FIRST_NAME; }
        @Override public Object generate(FieldDefinition f, GenerationContext c) {
            return c.getFaker().name().firstName();
        }
    }

    @Component
    public static class LastNameGenerator implements FieldGenerator {
        @Override public FieldType type() { return FieldType.LAST_NAME; }
        @Override public Object generate(FieldDefinition f, GenerationContext c) {
            return c.getFaker().name().lastName();
        }
    }

    @Component
    public static class FullNameGenerator implements FieldGenerator {
        @Override public FieldType type() { return FieldType.FULL_NAME; }
        @Override public Object generate(FieldDefinition f, GenerationContext c) {
            return c.getFaker().name().fullName();
        }
    }

    @Component
    public static class EmailGenerator implements FieldGenerator {
        @Override public FieldType type() { return FieldType.EMAIL; }
        @Override public Object generate(FieldDefinition f, GenerationContext c) {
            return c.getFaker().internet().emailAddress();
        }
    }

    @Component
    public static class PhoneGenerator implements FieldGenerator {
        @Override public FieldType type() { return FieldType.PHONE; }
        @Override public Object generate(FieldDefinition f, GenerationContext c) {
            return c.getFaker().phoneNumber().phoneNumber();
        }
    }

    /**
     * Date of birth. Config:
     *   - minAge (int, default 18)
     *   - maxAge (int, default 90)
     */
    @Component
    public static class DateOfBirthGenerator implements FieldGenerator {
        @Override public FieldType type() { return FieldType.DATE_OF_BIRTH; }
        @Override public Object generate(FieldDefinition f, GenerationContext c) {
            int minAge = ConfigReader.getInt(f.getConfig(), "minAge", 18);
            int maxAge = ConfigReader.getInt(f.getConfig(), "maxAge", 90);
            Date d = c.getFaker().date().birthday(minAge, maxAge);
            return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString();
        }
    }
}
