package org.example.services;

import java.util.List;
import java.util.Optional;

import org.example.models.Exercise;
import org.example.models.TrainingElement;
import org.example.repository.TrainingElementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TrainingElementService {

    private final TrainingElementRepository trainingElementRepository;

    public TrainingElement save(TrainingElement trainingElement) {
        return trainingElementRepository.save(trainingElement);
    }

    public List<TrainingElement> findAllByExercise(Exercise exercise){
        return trainingElementRepository.findAllByExercise(exercise);
    }

    @Transactional
    public TrainingElement getOrMake(
            final Exercise exercise,
            final int howMuchToDo,
            final int recommendedTimeToDoLessThan,
            final int timeToRestAfter)
    {
        Optional<TrainingElement> trainingElementOptional = trainingElementRepository
                .findByExerciseAndHowMuchToDoAndRecommendedTimeToDoAndTimeToRest(
                        exercise, howMuchToDo, recommendedTimeToDoLessThan, timeToRestAfter
                );
        if (trainingElementOptional.isPresent()) {
            return trainingElementOptional.get();
        }

        TrainingElement trainingElement = new TrainingElement();
        trainingElement.setExercise(exercise);
        trainingElement.setHowMuchToDo(howMuchToDo);
        trainingElement.setRecommendedTimeToDo(recommendedTimeToDoLessThan);
        trainingElement.setTimeToRest(timeToRestAfter);

        return trainingElement;
    }

    public void delete(TrainingElement trainingElement){
        trainingElementRepository.delete(trainingElement);
    }
}
