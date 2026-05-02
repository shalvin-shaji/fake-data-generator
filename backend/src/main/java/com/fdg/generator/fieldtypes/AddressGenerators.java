package com.fdg.generator.fieldtypes;

import com.fdg.generator.FieldGenerator;
import com.fdg.generator.GenerationContext;
import com.fdg.model.FieldDefinition;
import com.fdg.model.FieldType;
import org.springframework.stereotype.Component;

/** Address-related field generators. */
public final class AddressGenerators {

    @Component
    public static class StreetGenerator implements FieldGenerator {
        @Override public FieldType type() { return FieldType.STREET; }
        @Override public Object generate(FieldDefinition f, GenerationContext c) {
            return c.getFaker().address().streetAddress();
        }
    }

    @Component
    public static class CityGenerator implements FieldGenerator {
        @Override public FieldType type() { return FieldType.CITY; }
        @Override public Object generate(FieldDefinition f, GenerationContext c) {
            return c.getFaker().address().city();
        }
    }

    @Component
    public static class StateGenerator implements FieldGenerator {
        @Override public FieldType type() { return FieldType.STATE; }
        @Override public Object generate(FieldDefinition f, GenerationContext c) {
            return c.getFaker().address().state();
        }
    }

    @Component
    public static class CountryGenerator implements FieldGenerator {
        @Override public FieldType type() { return FieldType.COUNTRY; }
        @Override public Object generate(FieldDefinition f, GenerationContext c) {
            return c.getFaker().address().country();
        }
    }

    @Component
    public static class PostalCodeGenerator implements FieldGenerator {
        @Override public FieldType type() { return FieldType.POSTAL_CODE; }
        @Override public Object generate(FieldDefinition f, GenerationContext c) {
            return c.getFaker().address().zipCode();
        }
    }
}
