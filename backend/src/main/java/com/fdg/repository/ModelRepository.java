package com.fdg.repository;

import com.fdg.entity.ModelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelRepository extends JpaRepository<ModelEntity, String> {
}
