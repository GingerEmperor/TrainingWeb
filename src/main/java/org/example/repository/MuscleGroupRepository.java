package org.example.repository;

import org.example.models.muscles.Muscle;
import org.example.models.muscles.MuscleGroup;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MuscleGroupRepository extends CrudRepository<MuscleGroup, Long> {
    Optional<MuscleGroup> findByName(String name);

    List<MuscleGroup> findAll();

    Optional<MuscleGroup> findById(Long id);

}
