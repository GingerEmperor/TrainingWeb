package org.example.models;

import java.util.Set;

import javax.persistence.Cache;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;

import org.example.models.enums.Equipment;
import org.example.models.muscles.Muscle;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(exclude = "exerciseInfo")
@Entity
public class Exercise implements Comparable<Exercise> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Muscle> primaryWorkingMuscles;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Muscle> secondWorkingMuscles;

    private String title;

    private String image;

    private Equipment equipmentNeed;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "exercise")
    private ExerciseInfo exerciseInfo;

    public void setExerciseInfo(final ExerciseInfo exerciseInfo) {
        this.exerciseInfo = exerciseInfo;
        exerciseInfo.setExercise(this);
    }

    @Override
    public int compareTo(final Exercise o) {
        return this.getTitle().toLowerCase().charAt(0) - o.getTitle().toLowerCase().charAt(0);
    }

}
