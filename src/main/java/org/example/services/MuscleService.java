package org.example.services;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.example.exeptions.AlreadyExistsException;
import org.example.exeptions.CanNotDeleteException;
import org.example.exeptions.FileCanNotSaveException;
import org.example.exeptions.NotFoundException;
import org.example.models.Exercise;
import org.example.models.muscles.Muscle;
import org.example.models.muscles.MuscleGroup;
import org.example.repository.MuscleRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MuscleService {

    private final MuscleRepository muscleRepository;

    private final ExerciseService exerciseService;

    private final MuscleGroupService muscleGroupService;

    private final GlobalService globalService;

    @Value("${upload.musclePath}")
    private String uploadPath;

    public List<Muscle> findAll() {
        return muscleRepository.findAll();
    }

    public List<Muscle> findAllByMuscleGroup(Long muscleGroupId) {
        return muscleRepository.findAllByMuscleGroup_Id(muscleGroupId);
    }

    public Muscle findByName(String name) {
        return muscleRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Такой мышцы нет в базе двнных"));
    }

    public boolean checkIfExistsMuscleByName(String name) {
        try {
            findByName(name);
        } catch (NotFoundException e) {
            return true;
        }
        throw new AlreadyExistsException("Такое упражнение уже существует");
    }

    public Muscle createMuscle(MuscleGroup group, String name, String info, String muscleFunctions) {
        Muscle muscle = new Muscle();
        muscle.setMuscleGroup(group);
        muscle.setName(name);
        muscle.setInfo(info);
        muscle.setFunctions(muscleFunctions);
        return muscle;
    }

    public Muscle createMuscle(MuscleGroup group, String name, String info, String muscleFunctions, String image) {
        Muscle muscle = createMuscle(group, name, info, muscleFunctions);
        muscle.setImage(image);
        return muscle;
    }

    public void save(Muscle muscle) {
        muscleRepository.save(muscle);
    }

    public Muscle findById(long id) {
        return muscleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Такой мышцы нет"));
    }

    public Muscle addMuscle(
            String muscleGroupName,
            String muscleName,
            String muscleInfo,
            String muscleFunctions,
            MultipartFile image
    ) {
        Muscle muscleToAdd;

        checkIfExistsMuscleByName(muscleName);

        MuscleGroup currentMuscleGroup = muscleGroupService.findByName(muscleGroupName);

        try {
            muscleToAdd = createMuscle(
                    currentMuscleGroup, muscleName, muscleInfo, muscleFunctions,
                    globalService.saveImgToPathWithPrefixName(image, uploadPath, muscleName)
            );
        } catch (FileCanNotSaveException | IOException e) {
            muscleToAdd = createMuscle(
                    currentMuscleGroup, muscleName, muscleInfo, muscleFunctions
            );
        }
        save(muscleToAdd);
        muscleGroupService.addMusclesIntoGroup(muscleToAdd, currentMuscleGroup);
        return muscleToAdd;
    }

    public Muscle updateMuscle(long id, String newMuscleName, String newInfo, String newFunctions) {
        Muscle updatedMuscle = findById(id);
        updatedMuscle.setName(newMuscleName);
        updatedMuscle.setInfo(newInfo);
        updatedMuscle.setFunctions(newFunctions);
        return updatedMuscle;
    }

    public Muscle updateMuscle(
            long id,
            String newMuscleName,
            String newMuscleInfo,
            String newMuscleFunctions,
            MultipartFile newMuscleImage
    ) {

        Muscle updatedMuscle = updateMuscle(id, newMuscleName, newMuscleInfo, newMuscleFunctions);
        try {
            final String img = globalService.saveImgToPathWithPrefixName(newMuscleImage, uploadPath, newMuscleName);
            new File(uploadPath + "/" + updatedMuscle.getImage()).delete();
            updatedMuscle.setImage(img);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return updatedMuscle;
    }

    //
    public boolean deleteMuscleById(long id) {
        Muscle muscleToDelete = findById(id);
        MuscleGroup muscleGroup = muscleToDelete.getMuscleGroup();
        muscleGroup.getMuscleSet().remove(muscleToDelete);
        final Set<Exercise> exerciseByPrimaryWorkingMuscle = exerciseService.findAllByPrimaryWorkingMuscle(muscleToDelete);
        final Set<Exercise> exerciseBySecondaryWorkingMuscle = exerciseService.findAllBySecondaryWorkingMuscle(muscleToDelete);
        if (exerciseByPrimaryWorkingMuscle.isEmpty() && exerciseBySecondaryWorkingMuscle.isEmpty()) {
            new File(uploadPath + "/" + muscleToDelete.getImage()).delete();
            muscleRepository.delete(muscleToDelete);
        } else {
            throw new CanNotDeleteException("Нельзя удалить мышцу т.к. есть упражнения на неё");
        }

        return true;
    }

    public List<Muscle> findAllByMuscleGroup(MuscleGroup muscleGroup) {
        return muscleRepository.findAllByMuscleGroup(muscleGroup);
    }

}
