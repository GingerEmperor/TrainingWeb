package org.example.services;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.example.exeptions.AlreadyExistsException;
import org.example.exeptions.FileCanNotSaveException;
import org.example.exeptions.NotFoundException;
import org.example.models.Exercise;
import org.example.models.Training;
import org.example.models.TrainingElement;
import org.example.models.enums.Difficulty;
import org.example.models.enums.ForWho;
import org.example.models.enums.Goal;
import org.example.models.muscles.MuscleGroup;
import org.example.repository.TrainingRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Service
public class TrainingService {

    @Value("${upload.trainingPath}")
    private String uploadPath;

    private final TrainingRepository trainingRepository;

    private final TrainingElementService trainingElementService;

    private final GlobalService globalService;

    public List<Training> findAll() {
        return trainingRepository.findAll();
    }

    public Training findById(Long id) {
        return trainingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Такой тренировки не существует"));
    }

    public List<Training> findAllByPrimaryMuscleGroups(MuscleGroup muscleGroup) {
        return trainingRepository.findAllByPrimaryMuscleGroups(muscleGroup);
    }

    public List<Training> findAllByDifficulty(Difficulty difficulty) {
        return trainingRepository.findAllByDifficulty(difficulty);
    }

    public List<Training> findAllByGrade(final ForWho forWho) {
        return trainingRepository.findAllByForWho(forWho);
    }

    public List<Training> findAllByGoal(final Goal goal) {
        return trainingRepository.findAllByGoal(goal);
    }

    public Training save(Training training) {
        return trainingRepository.save(training);
    }

    public Training findByName(String name) {
        return trainingRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Такой тренировки нет в базе двнных"));
    }

    public List<Training> findByExercise(Exercise exercise) {
        Set<Training> trainings = new HashSet<>();
        trainingElementService.findAllByExercise(exercise).stream().forEach(trainingElement -> trainings.addAll(trainingRepository.findAllByTrainingElements(trainingElement)));
        System.out.println("============");
        System.out.println(trainings);
        return null;
    }

    public boolean checkIfExistsTrainingByName(String name) {
        try {
            findByName(name);
        } catch (NotFoundException e) {
            return true;
        }
        throw new AlreadyExistsException("Такая тренировка уже существует");
    }

    public Training createTraining(
            String trainingName, String forWho, String difficulty,
            String goal, List<TrainingElement> trainingElements,
            String someAdvice, MultipartFile image,
            List<MuscleGroup> primaryMuscleGroups, List<MuscleGroup> secondaryMuscleGroups) {

        Training training = Training.builder()
                .name(trainingName)
                .forWho(ForWho.valueOf(forWho))
                .difficulty(Difficulty.valueOf(difficulty))
                .goal(Goal.valueOf(goal))
                .trainingElements(trainingElements)
                .advice(someAdvice)
                .primaryMuscleGroups(primaryMuscleGroups)
                .secondaryMuscleGroups(secondaryMuscleGroups)
                .build();
        try {
            training.setImage(globalService.saveImgToPathWithPrefixName(image, uploadPath, trainingName));
        } catch (FileCanNotSaveException | IOException e) {
            e.printStackTrace();
        }
        return training;
    }

    public void deleteById(final long id) {
        Training trainingToDelete = findById(id);
        trainingToDelete.getTrainingElements().clear();
        trainingToDelete.getPrimaryMuscleGroups().clear();
        trainingToDelete.getSecondaryMuscleGroups().clear();
        File imgFile = new File((uploadPath + "/" + trainingToDelete.getImage()));
        imgFile.delete();
        trainingRepository.delete(trainingToDelete);
    }
}
