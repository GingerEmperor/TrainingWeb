package org.example.services;

import org.example.models.TrainingElement;
import org.example.repository.TrainingElementRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TrainingElementService {

    private final TrainingElementRepository trainingElementRepository;

    public TrainingElement save(TrainingElement trainingElement){
        return trainingElementRepository.save(trainingElement);
    }

}
