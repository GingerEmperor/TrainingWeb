package org.example.models.muscles;

import javax.persistence.*;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class MuscleGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    private String image;

    @OneToMany
    @JoinColumn(name = "muscle_id")
    Set<Muscle> muscleSet;

}
