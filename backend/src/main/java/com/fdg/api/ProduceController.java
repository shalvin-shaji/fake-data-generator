package com.fdg.api;

import com.fdg.model.ModelDefinition;
import com.fdg.service.KafkaProducerService;
import com.fdg.service.ModelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * POST /api/produce — generate records and send them to a Kafka topic.
 */
@RestController
@RequestMapping("/api/produce")
public class ProduceController {

    private final ModelService modelService;
    private final KafkaProducerService kafkaProducerService;

    public ProduceController(ModelService modelService, KafkaProducerService kafkaProducerService) {
        this.modelService = modelService;
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostMapping
    public ResponseEntity<ProduceResult> produce(@RequestBody ProduceRequest req) {
        ModelDefinition model = modelService.findById(req.getModelId()).orElse(null);
        if (model == null) {
            return ResponseEntity.notFound().build();
        }

        String topic = (req.getTopic() != null && !req.getTopic().isBlank())
                ? req.getTopic()
                : model.getKafkaTopic();

        if (topic == null || topic.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        int count = Math.clamp(req.getCount(), 1, 100_000);
        int sent = kafkaProducerService.produce(model, topic, count);
        return ResponseEntity.ok(new ProduceResult(model.getId(), topic, sent));
    }
}
