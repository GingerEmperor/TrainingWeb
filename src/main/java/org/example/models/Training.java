package org.example.models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;

import org.example.models.enums.Difficulty;
import org.example.models.enums.ForWho;
import org.example.models.enums.Goal;
import org.example.models.muscles.MuscleGroup;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToMany
    private List<TrainingElement> trainingElements;

    @ManyToMany
    private List<MuscleGroup> primaryMuscleGroups;

    @ManyToMany
    private List<MuscleGroup> secondaryMuscleGroups;

    @Lob
    private String advice;

    private Goal goal;

    private String image;

    private ForWho forWho;

    private Difficulty difficulty;

}

