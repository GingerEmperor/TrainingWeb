package org.example.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.example.models.Exercise;
import org.example.models.muscles.Muscle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    Optional<Exercise> findById(Long id);

    Exercise findByTitle(String title);

    Exercise findByPrimaryWorkingMusclesContaining(Muscle muscle);

    Set<Exercise> findAllByPrimaryWorkingMusclesContaining(Muscle muscle);


}
