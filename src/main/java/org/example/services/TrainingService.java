package org.example.services;

import java.util.List;

import org.example.exeptions.AlreadyExistsException;
import org.example.exeptions.NotFoundException;
import org.example.models.Training;
import org.example.models.muscles.Muscle;
import org.example.repository.TrainingRepository;
import org.springframework.stereotype.Service;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Service
public class TrainingService {

    private final TrainingRepository trainingRepository;

    public List<Training> findAll() {
        return trainingRepository.findAll();
    }

    public Training findById(Long id){
        return trainingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Такой тренировки не существует"));
    }

    public Training save(Training training){
        return trainingRepository.save(training);
    }


    public Training findByName(String name) {
        return trainingRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Такой тренировки нет в базе двнных"));
    }

    public boolean checkIfExistsTrainingByName(String name) {
        try {
            findByName(name);
        } catch (NotFoundException e) {
            return true;
        }
        throw new AlreadyExistsException("Такая тренировка уже существует");
    }
}
