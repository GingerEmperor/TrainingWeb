package org.example.services;

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

    @Value("${upload.path}")
    private String uploadPath;

    public List<Muscle> findAll() {
        return muscleRepository.findAll();
    }

    public List<Muscle> findAllByMuscleGroup(Long muscleGroupId) {
        return muscleRepository.findAllByMuscleGroup_Id(muscleGroupId);
    }

    public Muscle findByName(String name) {
        return muscleRepository.findByName(name);
    }

    public Muscle createMuscle(MuscleGroup group, String name, String info) {
        Muscle muscle = new Muscle();
        muscle.setMuscleGroup(group);
        muscle.setName(name);
        muscle.setInfo(info);
        return muscle;
    }

    public Muscle createMuscle(MuscleGroup group, String name, String info, String image) {
        Muscle muscle = createMuscle(group, name, info);
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
            MultipartFile image
    ) {
        Muscle muscleToAdd = findByName(muscleName);
        if (muscleToAdd != null) {
            throw new AlreadyExistsException("Мышца с таким именем уже существует");
        }
        MuscleGroup currentMuscleGroup = muscleGroupService.findByName(muscleGroupName);

        try {
            muscleToAdd = createMuscle(
                    currentMuscleGroup, muscleName, muscleInfo,
                    globalService.saveImgToPathWithPrefixName(image, uploadPath, muscleName)
            );
        } catch (FileCanNotSaveException | IOException e) {
            muscleToAdd = createMuscle(
                    currentMuscleGroup, muscleName, muscleInfo
            );
        }
        save(muscleToAdd);
        muscleGroupService.addMusclesIntoGroup(muscleToAdd, currentMuscleGroup);
        return muscleToAdd;
    }

    public boolean deleteMuscleById(long id) {
        Optional<Muscle> muscleOpt = muscleRepository.findById(id);
        if (muscleOpt.isPresent()) {
            Muscle muscle = muscleOpt.get();
            MuscleGroup muscleGroup = muscle.getMuscleGroup();
            muscleGroup.getMuscleSet().remove(muscle);
            final Set<Exercise> exerciseByPrimaryWorkingMuscle = exerciseService.findAllByPrimaryWorkingMuscle(muscle);
            final Set<Exercise> exerciseBySecondaryWorkingMuscle = exerciseService.findAllBySecondaryWorkingMuscle(muscle);
            if (exerciseByPrimaryWorkingMuscle.isEmpty() && exerciseBySecondaryWorkingMuscle.isEmpty()) {
                muscleRepository.delete(muscleOpt.get());
            } else {
                throw new CanNotDeleteException("Нельзя удалить мышцу т.к. есть упражнения на неё");
            }

            return true;
        }
        throw new CanNotDeleteException("Нельзя удалить мышцу т.к. её не сушествует");
    }

    public List<Muscle> findAllByMuscleGroup(MuscleGroup muscleGroup) {
        return muscleRepository.findAllByMuscleGroup(muscleGroup);
    }

}
