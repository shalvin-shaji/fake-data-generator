package com.fdg.service;

import com.fdg.entity.ModelEntity;
import com.fdg.model.ModelDefinition;
import com.fdg.repository.ModelRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ModelService {

    private final ModelRepository repository;
    private final ModelMapper mapper;

    public ModelService(ModelRepository repository, ModelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public List<ModelDefinition> findAll() {
        return repository.findAll().stream().map(mapper::toModel).toList();
    }

    public Optional<ModelDefinition> findById(String id) {
        return repository.findById(id).map(mapper::toModel);
    }

    public ModelDefinition create(ModelDefinition model) {
        if (model.getId() == null || model.getId().isBlank()) {
            model.setId(UUID.randomUUID().toString());
        }
        ModelEntity saved = repository.save(mapper.toEntity(model));
        return mapper.toModel(saved);
    }

    public Optional<ModelDefinition> update(String id, ModelDefinition model) {
        if (!repository.existsById(id)) {
            return Optional.empty();
        }
        model.setId(id);
        ModelEntity saved = repository.save(mapper.toEntity(model));
        return Optional.of(mapper.toModel(saved));
    }

    public boolean delete(String id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
}
