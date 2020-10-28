package org.example.repository;

import java.util.List;
import java.util.Optional;

import org.example.models.muscles.MuscleGroup;
import org.springframework.data.repository.CrudRepository;

public interface MuscleGroupRepository extends CrudRepository<MuscleGroup, Long> {
    Optional<MuscleGroup> findByName(String name);

    List<MuscleGroup> findAll();

    Optional<MuscleGroup> findById(Long id);

}
