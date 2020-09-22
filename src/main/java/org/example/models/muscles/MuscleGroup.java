package org.example.models.muscles;

import javax.persistence.*;
import java.util.Set;

@Entity
public class MuscleGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @OneToMany
    Set<Muscle> muscleSet;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Muscle> getMuscleSet() {
        return muscleSet;
    }

    public void setMuscleSet(Set<Muscle> muscleSet) {
        this.muscleSet = muscleSet;
    }
}
