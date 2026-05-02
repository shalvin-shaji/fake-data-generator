package com.fdg.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fdg.generator.GenerationContext;
import com.fdg.generator.ModelGeneratorEngine;
import com.fdg.model.ModelDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class KafkaProducerService {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ModelGeneratorEngine engine;
    private final ObjectMapper objectMapper;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate,
                                 ModelGeneratorEngine engine) {
        this.kafkaTemplate = kafkaTemplate;
        this.engine = engine;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Generates {@code count} records for the model and sends each one to
     * {@code topic}. The Kafka record key is the model's keyField value (if set),
     * otherwise null (round-robin partitioning).
     *
     * @return number of records successfully enqueued
     */
    public int produce(ModelDefinition model, String topic, int count) {
        GenerationContext ctx = new GenerationContext();
        int sent = 0;
        for (int i = 0; i < count; i++) {
            Map<String, Object> record = engine.generateOne(model, ctx);
            try {
                String json = objectMapper.writeValueAsString(record);
                String key = model.getKeyField() != null
                        ? String.valueOf(record.get(model.getKeyField()))
                        : null;
                kafkaTemplate.send(topic, key, json);
                sent++;
            } catch (Exception e) {
                log.error("Failed to send record {} to topic {}: {}", i, topic, e.getMessage());
            }
        }
        log.info("Produced {} records to topic '{}'", sent, topic);
        return sent;
    }
}
