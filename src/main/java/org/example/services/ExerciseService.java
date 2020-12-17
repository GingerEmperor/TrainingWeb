package org.example.services;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.example.exeptions.AlreadyExistsException;
import org.example.exeptions.FileCanNotSaveException;
import org.example.exeptions.NotFoundException;
import org.example.models.Exercise;
import org.example.models.ExerciseInfo;
import org.example.models.enums.Equipment;
import org.example.models.muscles.Muscle;
import org.example.models.muscles.MuscleGroup;
import org.example.repository.ExerciseRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Service
@Data
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;

    private final GlobalService globalService;

    @Value("${upload.exercisePath}")
    private String uploadPath;

    public Exercise findByTitle(final String exerciseTitle) {
        return exerciseRepository.findByTitle(exerciseTitle)
                .orElseThrow(() -> new NotFoundException("Такое упраднения нет в БД"));
    }

    public boolean checkIfExistsExerciseByTitle(String title) {
        try {
            findByTitle(title);
        } catch (NotFoundException e) {
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

    private Exercise createNewExercise(
            String title,
            Set<Muscle> primaryMuscles,
            Set<Muscle> secondaryMuscles,
            String exerciseInfo,
            String howToDo,
            String videoLink,
            Equipment equipment,
            String imageStart,
            String imageFinish,
            String image
            ) {

        Exercise exercise = createNewExercise(
                title, primaryMuscles, secondaryMuscles,
                exerciseInfo, howToDo, videoLink, equipment
        );
        exercise.setImage(image);
        exercise.setImageStart(imageStart);
        exercise.setImageFinish(imageFinish);
        return exercise;
    }

    private Exercise createNewExercise(
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

    private Exercise createNewExercise(
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

    ///

    public Exercise createNewExercise(
            String exerciseTitle,
            Set<Muscle> primaryMuscleSet,
            Set<Muscle> secondaryMuscleSet,
            String exerciseInfo,
            String howToDo,
            String videoLink,
            Equipment equipment,
            MultipartFile imgFile,
            MultipartFile imgFile1,
            MultipartFile imgFile2
    ) {
        Exercise newExerciseToAdd;
        checkIfExistsExerciseByTitle(exerciseTitle);
        try {
            try {
                newExerciseToAdd = createNewExercise(
                        exerciseTitle, primaryMuscleSet, secondaryMuscleSet,
                        exerciseInfo, howToDo, videoLink, equipment,
                        globalService.saveImgToPathWithPrefixName(imgFile1, uploadPath, exerciseTitle + "_start"),
                        globalService.saveImgToPathWithPrefixName(imgFile2, uploadPath, exerciseTitle + "_finish"),
                        globalService.saveImgToPathWithPrefixName(imgFile, uploadPath, exerciseTitle)//
                        );
            }catch (FileCanNotSaveException | IOException f){
                System.out.println("Cant add image but ok");
                f.printStackTrace();
                System.out.println("Cant add image but ok");
                newExerciseToAdd = createNewExercise(
                        exerciseTitle, primaryMuscleSet, secondaryMuscleSet,
                        exerciseInfo, howToDo, videoLink, equipment,
                        globalService.saveImgToPathWithPrefixName(imgFile, uploadPath, exerciseTitle));
            }
        } catch (FileCanNotSaveException | IOException f) {
            System.out.println("Cant add image but ok");
            f.printStackTrace();
            System.out.println("Cant add image but ok");
            newExerciseToAdd = createNewExercise(exerciseTitle, primaryMuscleSet, secondaryMuscleSet,
                    exerciseInfo, howToDo, videoLink, equipment);
        }
        save(newExerciseToAdd);
        return newExerciseToAdd;
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
        Exercise exerciseToDelete = findById(id);
        exerciseToDelete.getPrimaryWorkingMuscles().clear();
        exerciseToDelete.getSecondWorkingMuscles().clear();
        File imgFile = new File((uploadPath + "/" + exerciseToDelete.getImage()));
        File imgStartFile = new File((uploadPath + "/" + exerciseToDelete.getImageStart()));
        File imgFinishFile = new File((uploadPath + "/" + exerciseToDelete.getImageFinish()));
        imgFile.delete();
        imgStartFile.delete();
        imgFinishFile.delete();
        exerciseRepository.delete(exerciseToDelete);
    }

    private Exercise updateExercise(Exercise exerciseToUpdate,
            final String exerciseTitle,
            final Set<Muscle> primaryMuscleSet,
            final Set<Muscle> secondaryMuscleSet,
            final String exerciseInfo,
            final String howToDo,
            final String videoLink,
            final Equipment equipment) {
        exerciseToUpdate.setTitle(exerciseTitle);
        exerciseToUpdate.setPrimaryWorkingMuscles(primaryMuscleSet);
        exerciseToUpdate.setSecondWorkingMuscles(secondaryMuscleSet);
        exerciseToUpdate.getExerciseInfo().setSomeInfo(exerciseInfo);
        exerciseToUpdate.getExerciseInfo().setHowToDo(howToDo);
        exerciseToUpdate.getExerciseInfo().setVideoLink(videoLink);
        exerciseToUpdate.setEquipmentNeed(equipment);

        return exerciseToUpdate;

    }

    private Exercise updateExercise(Exercise exerciseToUpdate,
            final String exerciseTitle,
            final Set<Muscle> primaryMuscleSet,
            final Set<Muscle> secondaryMuscleSet,
            final String exerciseInfo,
            final String howToDo,
            final String videoLink,
            final Equipment equipment,
            final String image) {
        Exercise exercise = updateExercise(exerciseToUpdate, exerciseTitle,
                primaryMuscleSet, secondaryMuscleSet, exerciseInfo,
                howToDo, videoLink, equipment);
        exercise.setImage(image);

        return exercise;
    }

    public Exercise updateExercise(Long exerciseToUpdateId,
            final String exerciseTitle,
            final Set<Muscle> primaryMuscleSet,
            final Set<Muscle> secondaryMuscleSet,
            final String exerciseInfo,
            final String howToDo,
            final String videoLink,
            final Equipment equipment,
            final MultipartFile previewImg) {
        Exercise exerciseToUpdate = findById(exerciseToUpdateId);
        Exercise updatedExercise = exerciseToUpdate;
        File oldImageToDelete=new File((uploadPath + "/" + exerciseToUpdate.getImage()));
        oldImageToDelete.delete();
        try {
            updatedExercise = updateExercise(exerciseToUpdate, exerciseTitle,
                    primaryMuscleSet, secondaryMuscleSet, exerciseInfo,
                    howToDo, videoLink, equipment,
                    globalService.saveImgToPathWithPrefixName(previewImg, uploadPath, exerciseTitle));


        } catch (Exception e) {
            System.out.println("Cant update image");
            updatedExercise = updateExercise(exerciseToUpdate, exerciseTitle,
                    primaryMuscleSet, secondaryMuscleSet, exerciseInfo,
                    howToDo, videoLink, equipment);
        }
        save(updatedExercise);

        return updatedExercise;
    }
}
