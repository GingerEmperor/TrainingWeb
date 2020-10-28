package org.example.services;

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

    @Value("${upload.path}")
    private String uploadPath;

    public MuscleGroup findByName(String name) {
        return muscleGroupRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Такой группы мышц не существует"));
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
        try {
            currentMuscleGroup = findByName(muscleGroupName);
        } catch (NotFoundException e) {
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
        throw new AlreadyExistsException("Такая группа мыщц уже существует");
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
        // Optional<MuscleGroup> muscleGroup = muscleGroupRepository.findById(id);
        // if (muscleGroup.isPresent()) {
        //     return muscleGroup.get();
        // } else {
        //     throw new MuscleGroupNotFoundException("Такой группы мыщц не существует");
        // }
        //            throw new RuntimeException("Нет такой группы мышц");
        return muscleGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Такой группы мыщц не существует"));
    }

    public boolean deleteMuscleGroup(MuscleGroup muscleGroup) {
        MuscleGroup muscleGroupToDelete = findById(muscleGroup.getId());
        if (muscleGroupToDelete.getMuscleSet().isEmpty()) {
            muscleGroupRepository.delete(muscleGroupToDelete);
            return true;
        } else {
            throw new CanNotDeleteException("Группв мышц содержит мышцы");
        }
    }

    public void save(final MuscleGroup muscleGroup) {
        muscleGroupRepository.save(muscleGroup);
    }
}
