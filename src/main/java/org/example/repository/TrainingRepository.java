package org.example.repository;

import java.util.List;
import java.util.Optional;

import org.example.models.Training;
import org.example.models.TrainingElement;
import org.example.models.enums.Difficulty;
import org.example.models.enums.ForWho;
import org.example.models.enums.Goal;
import org.example.models.muscles.MuscleGroup;
import org.springframework.data.repository.CrudRepository;

public interface TrainingRepository extends CrudRepository<Training, Long> {

    List<Training> findAll();

    Optional<Training> findByName(String name);

    List<Training> findAllByPrimaryMuscleGroups(MuscleGroup muscleGroup);

    List<Training> findAllByTrainingElements(TrainingElement trainingElement);

    List<Training> findAllByDifficulty(Difficulty difficulty);

    List<Training> findAllByForWho(ForWho forWho);

    List<Training> findAllByGoal(Goal goal);
}
