package org.example.repository;

import java.util.List;

import org.example.models.Training;
import org.springframework.data.repository.CrudRepository;

public interface TrainingRepository extends CrudRepository<Training,Long> {
    List<Training> findAll();
}
