package org.example.models;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import org.example.models.enums.Equipment;
import org.example.models.muscles.Muscle;

import lombok.Data;

@Entity
@Data
public class Exercise implements Comparable<Exercise> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToMany
    private Set<Muscle> primaryWorkingMuscles;

    @ManyToMany
    private Set<Muscle> secondWorkingMuscles;

    private String title;

    private String info;

    private String image;

    private Equipment equipmentNeed;

    @Override
    public int compareTo(final Exercise o) {
        return this.getTitle().toLowerCase().charAt(0)-o.getTitle().toLowerCase().charAt(0);
    }
}
