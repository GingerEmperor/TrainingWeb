package org.example.models.muscles;

import javax.persistence.*;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class MuscleGroup implements Comparable<MuscleGroup> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String image;

    @OneToMany(mappedBy = "muscleGroup")
    Set<Muscle> muscleSet;

    @Override
    public int compareTo(final MuscleGroup o) {
        return this.getName().toLowerCase().charAt(0)-o.getName().toLowerCase().charAt(0);
    }

    @Override
    public String toString() {
        return getName();
    }
}
