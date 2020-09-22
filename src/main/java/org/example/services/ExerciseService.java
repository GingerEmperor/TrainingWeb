package org.example.services;
//
//import org.example.models.Category;
//import org.example.models.Exercise;
//import org.example.models.muscles.Muscle;
//import org.example.repository.ExerciseRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.Set;
//
//@Service
//public class ExerciseService {
//    @Autowired
//    private ExerciseRepository exerciseRepository;
//
//
//    public boolean addExercise(String exerciseName,
//                               Set<Muscle> muscleGroup,
//                               Category category,
//                               String info){
//        if(exerciseRepository.findByName(exerciseName)!=null){
//
//            return false;
//        }
//
//        Exercise exercise=new Exercise();
//
//        exerciseRepository.save(exercise);
//        return true;
//    }
//
//}
