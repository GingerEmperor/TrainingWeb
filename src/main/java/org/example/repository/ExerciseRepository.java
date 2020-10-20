package org.example.repository;

import java.util.Optional;

import org.example.models.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    Optional<Exercise> findById(Long id);

    Exercise findByTitle(String title);
}
