package org.example.repository;

import org.example.models.muscles.Muscle;
import org.example.models.muscles.MuscleGroup;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface MuscleRepository extends CrudRepository<Muscle,Long> {
    List<Muscle>findAll();
    Muscle findByName(String name);

    List<Muscle> findAllByMuscleGroup(MuscleGroup muscleGroup);

    List<Muscle> findAllByMuscleGroup_Id(Long muscleGroupId);

}
