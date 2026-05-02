package com.fdg.api;

import com.fdg.generator.GenerationContext;
import com.fdg.generator.ModelGeneratorEngine;
import com.fdg.model.ModelDefinition;
import com.fdg.service.ModelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * POST /api/generate — load a model from DB, generate N records,
 * return them as a JSON array. No Kafka involved; for preview/testing.
 */
@RestController
@RequestMapping("/api/generate")
public class GenerationController {

    private final ModelService modelService;
    private final ModelGeneratorEngine engine;

    public GenerationController(ModelService modelService, ModelGeneratorEngine engine) {
        this.modelService = modelService;
        this.engine = engine;
    }

    @PostMapping
    public ResponseEntity<List<Map<String, Object>>> generate(@RequestBody GenerateRequest req) {
        ModelDefinition model = modelService.findById(req.getModelId())
                .orElse(null);
        if (model == null) {
            return ResponseEntity.notFound().build();
        }
        int count = Math.max(1, Math.min(req.getCount(), 10_000));
        List<Map<String, Object>> records = engine.generateMany(model, count, new GenerationContext());
        return ResponseEntity.ok(records);
    }
}
