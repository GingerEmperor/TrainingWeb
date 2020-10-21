package org.example.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.example.exeptions.ExerciseNotFoundException;
import org.example.models.Exercise;
import org.example.models.enums.Equipment;
import org.example.models.muscles.Muscle;
import org.example.repository.ExerciseRepository;
import org.springframework.stereotype.Service;

import lombok.Data;

@Service
@Data
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    public List<Exercise> findAll() {
        return exerciseRepository.findAll();
    }

    public Exercise findById(Long id) {
        Optional<Exercise> exerciseOptional = exerciseRepository.findById(id);
        if (exerciseOptional.isPresent()) {
            return exerciseOptional.get();
        }
        throw new ExerciseNotFoundException();
    }

    public Exercise createNewExercise(
            String title,
            Set<Muscle> primaryMuscles,
            Set<Muscle> secondaryMuscles,
            String exerciseInfo,
            Equipment equipment,
            String image
    ) {

        Exercise exercise = createNewExercise(title, primaryMuscles, secondaryMuscles, exerciseInfo, equipment);
        exercise.setImage(image);
        return exercise;
    }

    public Exercise createNewExercise(
            final String exerciseTitle,
            final Set<Muscle> primaryMuscleSet,
            final Set<Muscle> secondaryMuscleSet,
            final String exerciseInfo,
            final Equipment equipment
    ) {
        Exercise exercise = new Exercise();
        exercise.setTitle(exerciseTitle);
        exercise.setPrimaryWorkingMuscles(primaryMuscleSet);
        exercise.setSecondWorkingMuscles(secondaryMuscleSet);
        exercise.setInfo(exerciseInfo);
        exercise.setEquipmentNeed(equipment);
        return exercise;
    }

    public Exercise findByTitle(final String exerciseTitle) {
        return exerciseRepository.findByTitle(exerciseTitle);
    }

    public void save(Exercise exercise){
        exerciseRepository.save(exercise);
    }
}
