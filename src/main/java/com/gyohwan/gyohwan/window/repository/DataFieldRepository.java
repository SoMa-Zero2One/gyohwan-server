package com.gyohwan.gyohwan.window.repository;

import com.gyohwan.gyohwan.window.domain.DataField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DataFieldRepository extends JpaRepository<DataField, Long> {
    List<DataField> findByEntityType(DataField.EntityType entityType);
}

