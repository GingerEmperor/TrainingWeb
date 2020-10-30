package org.example.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.example.models.Exercise;
import org.example.models.enums.Equipment;
import org.example.models.muscles.Muscle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    List<Exercise> findAll();

    Optional<Exercise> findById(Long id);

    Optional<Exercise> findByTitle(String title);

    Exercise findByPrimaryWorkingMusclesContaining(Muscle muscle);

    Set<Exercise> findAllByPrimaryWorkingMusclesContaining(Muscle muscle);
    Set<Exercise> findAllBySecondWorkingMusclesContaining(Muscle muscle);

    Set<Exercise> findAllByEquipmentNeed(Equipment equipment);


}
