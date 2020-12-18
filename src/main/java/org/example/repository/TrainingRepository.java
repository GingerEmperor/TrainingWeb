package org.example.repository;

import java.util.List;
import java.util.Optional;

import org.example.models.Exercise;
import org.example.models.Training;
import org.example.models.TrainingElement;
import org.example.models.muscles.MuscleGroup;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface TrainingRepository extends CrudRepository<Training,Long> {
    List<Training> findAll();

    Optional<Training> findByName(String name);

    List<Training> findAllByPrimaryMuscleGroups(MuscleGroup muscleGroup);

    List<Training> findAllByTrainingElements(TrainingElement trainingElement);
}
