package org.example.services;

import org.example.models.muscles.Muscle;
import org.example.models.muscles.MuscleGroup;
import org.example.repository.MuscleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class MuscleService {
    @Autowired
    MuscleRepository muscleRepository;

    public List<Muscle> findAll(){
        return muscleRepository.findAll();
    }

    public List<Muscle> findAllByMuscleGroup(Long muscleGroupId){
        return muscleRepository.findAllByMuscleGroup_Id(muscleGroupId);
    }

    public Muscle findByName(String name){return muscleRepository.findByName(name);}

    public Muscle createMuscle(MuscleGroup group, String name, String info){
        Muscle muscle=new Muscle();
        muscle.setMuscleGroup(group);
        muscle.setName(name);
        muscle.setInfo(info);
        return muscle;
    }

    public Muscle createMuscle(MuscleGroup group, String name, String info,String image){
        Muscle muscle= createMuscle(group, name, info);
        muscle.setImage(image);
        return muscle;
    }

    public void save(Muscle muscle){
        muscleRepository.save(muscle);
    }

    public Muscle findById(long id){
        Optional<Muscle> muscle=muscleRepository.findById(id);
        if(muscle.isPresent()) {
            return muscle.get();
        }
        throw new RuntimeException();
    }

    public boolean deleteMuscleById(long id){
        Optional<Muscle> muscleOpt=muscleRepository.findById(id);
        if(muscleOpt.isPresent()){
            Muscle muscle=muscleOpt.get();
            MuscleGroup muscleGroup=muscle.getMuscleGroup();
            muscleGroup.getMuscleSet().remove(muscle);
            muscleRepository.delete(muscleOpt.get());
            return true;
        }
        return false;
    }

    public List<Muscle> findAllByMuscleGroup(MuscleGroup muscleGroup){
        return muscleRepository.findAllByMuscleGroup(muscleGroup);
    }

}
