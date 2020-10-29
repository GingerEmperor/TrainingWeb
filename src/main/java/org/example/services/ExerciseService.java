package org.example.services;

import java.util.HashSet;
import java.util.Set;

import org.example.exeptions.AlreadyExistsException;
import org.example.exeptions.NotFoundException;
import org.example.models.Exercise;
import org.example.models.ExerciseInfo;
import org.example.models.enums.Equipment;
import org.example.models.muscles.Muscle;
import org.example.models.muscles.MuscleGroup;
import org.example.repository.ExerciseRepository;
import org.springframework.stereotype.Service;

import lombok.Data;

@Service
@Data
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;


    public Exercise findByTitle(final String exerciseTitle) {
        return exerciseRepository.findByTitle(exerciseTitle)
                .orElseThrow(() -> new NotFoundException("Такое упраднение уже есть"));
    }

    public boolean checkIfExistsExerciseByTitle(String title){
        try {
            findByTitle(title);
        }catch (NotFoundException e){
            return true;
        }
        throw new AlreadyExistsException("Такое упражнение уже существует");
    }

    public Set<Exercise> findAll() {
        return new HashSet<>(exerciseRepository.findAll());
    }

    public Exercise findById(Long id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Такого упражнения не существует"));
    }

    public Exercise createNewExercise(
            String title,
            Set<Muscle> primaryMuscles,
            Set<Muscle> secondaryMuscles,
            String exerciseInfo,
            String howToDo,
            String videoLink,
            Equipment equipment,
            String image
    ) {

        Exercise exercise = createNewExercise(
                title, primaryMuscles, secondaryMuscles,
                exerciseInfo, howToDo, videoLink, equipment
        );
        exercise.setImage(image);
        return exercise;
    }

    public Exercise createNewExercise(
            final String exerciseTitle,
            final Set<Muscle> primaryMuscleSet,
            final Set<Muscle> secondaryMuscleSet,
            final String exerciseInfo,
            final String howToDo,
            final String videoLink,
            final Equipment equipment
    ) {
        Exercise exercise = new Exercise();
        exercise.setTitle(exerciseTitle);
        exercise.setPrimaryWorkingMuscles(primaryMuscleSet);
        exercise.setSecondWorkingMuscles(secondaryMuscleSet);
        exercise.setExerciseInfo(new ExerciseInfo());
        exercise.getExerciseInfo().setExercise(exercise);
        exercise.getExerciseInfo().setSomeInfo(exerciseInfo);
        exercise.getExerciseInfo().setHowToDo(howToDo);
        exercise.getExerciseInfo().setVideoLink(videoLink);
        exercise.setEquipmentNeed(equipment);
        return exercise;
    }

    public Set<Exercise> findAllByPrimaryWorkingMuscle(Muscle muscle) {
        return exerciseRepository.findAllByPrimaryWorkingMusclesContaining(muscle);
    }

    public Set<Exercise> findAllBySecondaryWorkingMuscle(final Muscle muscle) {
        return exerciseRepository.findAllBySecondWorkingMusclesContaining(muscle);
    }

    public Set<Exercise> findAllByPrimaryWorkingMuscleGroup(MuscleGroup muscleGroup) {
        Set<Exercise> resultEx = new HashSet<>();
        for (Muscle m : muscleGroup.getMuscleSet()) {
            final Set<Exercise> allByPrimaryWorkingMuscle = findAllByPrimaryWorkingMuscle(m);
            resultEx.addAll(allByPrimaryWorkingMuscle);
        }
        return resultEx;
    }

    public Set<Exercise> findAllByEquipment(Equipment equipment) {
        return exerciseRepository.findAllByEquipmentNeed(equipment);
    }

    public void save(Exercise exercise) {
        exerciseRepository.save(exercise);
    }

    public void deleteExerciseById(final long id) {
        Exercise exerciseToDelete=findById(id);
        exerciseToDelete.getPrimaryWorkingMuscles().clear();
        exerciseToDelete.getSecondWorkingMuscles().clear();
        exerciseRepository.delete(exerciseToDelete);
    }
}
