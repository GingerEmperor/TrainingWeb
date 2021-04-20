package org.example.services;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.example.exeptions.AlreadyExistsException;
import org.example.exeptions.CanNotDeleteException;
import org.example.exeptions.FileCanNotSaveException;
import org.example.exeptions.NotFoundException;
import org.example.models.muscles.Muscle;
import org.example.models.muscles.MuscleGroup;
import org.example.repository.MuscleGroupRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MuscleGroupService {

    private final MuscleGroupRepository muscleGroupRepository;

    private final GlobalService globalService;

    private final ExerciseService exerciseService;

    @Value("${upload.muscleGroupPath}")
    private String uploadPath;

    public MuscleGroup findByName(String name) {
        return muscleGroupRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Такой группы мышц не существует"));
    }

    public boolean checkIfExistsMuscleGroupByName(String name) {
        try {
            findByName(name);
        } catch (NotFoundException e) {
            return true;
        }
        throw new AlreadyExistsException("Такое группа мышц уже существует");
    }

    private MuscleGroup createNewMuscleGroup(String name) {
        MuscleGroup muscleGroup = new MuscleGroup();
        muscleGroup.setName(name);
        return muscleGroup;
    }

    private MuscleGroup createNewMuscleGroup(String name, String image) {
        MuscleGroup muscleGroup = createNewMuscleGroup(name);
        muscleGroup.setImage(image);
        return muscleGroup;
    }

    public MuscleGroup createNewMuscleGroup(String muscleGroupName, MultipartFile file) {
        MuscleGroup currentMuscleGroup;
        checkIfExistsMuscleGroupByName(muscleGroupName);
        try {
            currentMuscleGroup = createNewMuscleGroup(
                    muscleGroupName,
                    globalService.saveImgToPathWithPrefixName(file, uploadPath, muscleGroupName));
        } catch (FileCanNotSaveException | IOException f) {
            currentMuscleGroup = createNewMuscleGroup(muscleGroupName);
        }
        save(currentMuscleGroup);
        return currentMuscleGroup;

    }

    public MuscleGroup updateMuscleGroup(long id, String muscleGroupName) {
        MuscleGroup updatedMuscleGroup = findById(id);
        updatedMuscleGroup.setName(muscleGroupName);
        return updatedMuscleGroup;
    }

    public MuscleGroup updateMuscleGroup(long id, String muscleGroupName, MultipartFile image) {
        MuscleGroup updatedMuscleGroup = updateMuscleGroup(id, muscleGroupName);
        try {
            final String img = globalService.saveImgToPathWithPrefixName(image, uploadPath, muscleGroupName);
            new File(uploadPath + "/" + updatedMuscleGroup.getName()).delete();
            updatedMuscleGroup.setImage(img);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return updatedMuscleGroup;
    }

    public boolean addMusclesIntoGroup(Muscle muscle, MuscleGroup muscleGroup) {
        muscleGroup.getMuscleSet().add(muscle);
        muscleGroupRepository.save(muscleGroup);
        return true;
    }

    public List<MuscleGroup> findAll() {
        return muscleGroupRepository.findAll();
    }

    public MuscleGroup findById(Long id) throws NotFoundException {
        return muscleGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Такой группы мыщц не существует"));
    }

    public boolean deleteMuscleGroup(MuscleGroup muscleGroup) {
        MuscleGroup muscleGroupToDelete = findById(muscleGroup.getId());
        tryToDeleteMuscleGroup(muscleGroupToDelete);
        exerciseService.checkIfExistsExercisesByMuscleGroup(muscleGroupToDelete);
        File imgFile = new File((uploadPath + "/" + muscleGroupToDelete.getImage()));
        imgFile.delete();
        muscleGroupRepository.delete(muscleGroupToDelete);
        return true;
    }

    public void save(final MuscleGroup muscleGroup) {
        muscleGroupRepository.save(muscleGroup);
    }

    public boolean isAnyExerciseWithThisMuscleGroup(MuscleGroup muscleGroup) {
        return !exerciseService.findAllByAnyWorkingMuscleGroup(muscleGroup).isEmpty();
    }

    public void tryToDeleteMuscleGroup(MuscleGroup muscleGroupTryToDelete) {
        if (!muscleGroupTryToDelete.getMuscleSet().isEmpty()) {
            throw new CanNotDeleteException("Группа - " + muscleGroupTryToDelete.getName() +
                    " мышц содержит мышцы - " + muscleGroupTryToDelete.getMuscleSet());
        }
        if (exerciseService.checkIfExistsExercisesByMuscleGroup(muscleGroupTryToDelete)) {
            throw new CanNotDeleteException("Есть упражнения с этой  " + muscleGroupTryToDelete.getName() +
                    "  группой мышц " + exerciseService.findAllByAnyWorkingMuscleGroup(muscleGroupTryToDelete));
        }
    }
}
