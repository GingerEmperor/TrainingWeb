package org.example.repository;

import java.util.Optional;

import org.example.models.Exercise;
import org.example.models.TrainingElement;
import org.springframework.data.repository.CrudRepository;

public interface TrainingElementRepository extends CrudRepository<TrainingElement, Long> {

    Optional<TrainingElement> findByExerciseAndHowMuchToDoAndRecommendedTimeToDoAndTimeToRest(
            Exercise exercise, int howMuchToDo, int recommendedTimeToDo, int timeToRest
    );

}
