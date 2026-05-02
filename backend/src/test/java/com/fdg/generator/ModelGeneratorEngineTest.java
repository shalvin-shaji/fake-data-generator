package com.fdg.generator;

import com.fdg.generator.fieldtypes.FinanceGenerators;
import com.fdg.generator.fieldtypes.IdentifierGenerators;
import com.fdg.generator.fieldtypes.MiscGenerators;
import com.fdg.generator.fieldtypes.PersonalGenerators;
import com.fdg.generator.fieldtypes.PrimitiveGenerators;
import com.fdg.model.FieldDefinition;
import com.fdg.model.FieldType;
import com.fdg.model.ModelDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ModelGeneratorEngineTest {

    private ModelGeneratorEngine engine;
    private GenerationContext ctx;

    @BeforeEach
    void setUp() {
        FieldGeneratorRegistry registry = new FieldGeneratorRegistry(List.of(
                new IdentifierGenerators.UuidGenerator(),
                new IdentifierGenerators.SequenceGenerator(),
                new IdentifierGenerators.ReferenceGenerator(),
                new PrimitiveGenerators.IntegerGenerator(),
                new PrimitiveGenerators.DecimalGenerator(),
                new PersonalGenerators.FirstNameGenerator(),
                new PersonalGenerators.EmailGenerator(),
                new FinanceGenerators.IbanGenerator(),
                new MiscGenerators.EnumGenerator(),
                new MiscGenerators.TimestampGenerator()
        ));
        registry.init();
        engine = new ModelGeneratorEngine(registry);
        ctx = new GenerationContext();
    }

    @Test
    void generatesAllFieldsForACustomerModel() {
        ModelDefinition customer = ModelDefinition.builder()
                .id("m1").name("Customer").kafkaTopic("customers").keyField("id")
                .fields(List.of(
                        FieldDefinition.builder().name("id").type(FieldType.UUID).build(),
                        FieldDefinition.builder().name("firstName").type(FieldType.FIRST_NAME).build(),
                        FieldDefinition.builder().name("email").type(FieldType.EMAIL).build()
                ))
                .build();

        Map<String, Object> rec = engine.generateOne(customer, ctx);

        assertThat(rec).containsKeys("id", "firstName", "email");
        assertThat(rec.get("id")).isInstanceOf(String.class);
        assertThat((String) rec.get("email")).contains("@");
    }

    @Test
    void referenceFieldPicksFromParentPool() {
        ModelDefinition customer = ModelDefinition.builder()
                .id("m1").name("Customer").keyField("customerId")
                .fields(List.of(
                        FieldDefinition.builder().name("customerId").type(FieldType.UUID).build()
                ))
                .build();

        ModelDefinition account = ModelDefinition.builder()
                .id("m2").name("Account").keyField("accountId")
                .fields(List.of(
                        FieldDefinition.builder().name("accountId").type(FieldType.UUID).build(),
                        FieldDefinition.builder()
                                .name("customerId")
                                .type(FieldType.REFERENCE)
                                .config(Map.of("modelName", "Customer", "field", "customerId"))
                                .build()
                ))
                .build();

        // Generate parents first to populate the pool.
        List<Map<String, Object>> customers = engine.generateMany(customer, 10, ctx);
        List<Object> parentIds = customers.stream().map(c -> c.get("customerId")).toList();

        // Now generate children and verify they reference real parents.
        for (int i = 0; i < 50; i++) {
            Map<String, Object> acc = engine.generateOne(account, ctx);
            assertThat(parentIds).contains(acc.get("customerId"));
        }
    }

    @Test
    void nullProbabilityProducesSomeNulls() {
        ModelDefinition m = ModelDefinition.builder()
                .id("m").name("M")
                .fields(List.of(
                        FieldDefinition.builder()
                                .name("maybe").type(FieldType.FIRST_NAME)
                                .nullProbability(1.0) // always null
                                .build()
                ))
                .build();

        Map<String, Object> rec = engine.generateOne(m, ctx);
        assertThat(rec.get("maybe")).isNull();
    }
}
