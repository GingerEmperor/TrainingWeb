package org.example.models;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.example.models.enums.Equipment;
import org.example.models.muscles.Muscle;

import lombok.Data;

@Entity
@Data
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany
    private Set<Muscle> primaryWorkingMuscles;

    @OneToMany
    private Set<Muscle> secondWorkingMuscles;

    private String title;

    private String info;

    private String image;

    private Equipment equipmentNeed;

}
