package org.example.services;

import org.example.models.muscles.Muscle;
import org.example.models.muscles.MuscleGroup;
import org.example.repository.MuscleGroupRepository;
import org.example.repository.MuscleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class MuscleGroupService {
    @Autowired
    private MuscleGroupRepository muscleGroupRepository;

    public MuscleGroup findByName(String name){
        Optional<MuscleGroup> muscleGroup=muscleGroupRepository.findByName(name);
        if (muscleGroup.isPresent())
            return muscleGroup.get();
//        else throw new RuntimeException("Такой группы мышц нет");
        return null;
//        return muscleGroupRepository.findByName(name);
    }

    public boolean createNewMuscleGroup(String name){
        MuscleGroup muscleGroup=new MuscleGroup();
        muscleGroup.setName(name);
        muscleGroupRepository.save(muscleGroup);
        return true;
    }

    public boolean addMusclesIntoGroup(Muscle muscle, MuscleGroup muscleGroup){
        muscleGroup.getMuscleSet().add(muscle);
        muscleGroupRepository.save(muscleGroup);
        return true;
    }

    public List<MuscleGroup> findAll(){
        return muscleGroupRepository.findAll();
    }

    public MuscleGroup findMuscleGroupById(Long id){
        Optional<MuscleGroup> muscleGroup=muscleGroupRepository.findById(id);
        if (muscleGroup.isPresent())
            return muscleGroup.get();
        else
            throw new RuntimeException("Нет такой группы мышц");
    }

    public boolean deleteMuscleGroup(MuscleGroup muscleGroup){
        muscleGroupRepository.delete(muscleGroup);
        return true;
    }

}
