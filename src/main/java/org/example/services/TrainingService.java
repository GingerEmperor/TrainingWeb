package org.example.services;

import java.util.List;

import org.example.models.Training;
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

    public Training save(Training training){
        return trainingRepository.save(training);
    }
}
