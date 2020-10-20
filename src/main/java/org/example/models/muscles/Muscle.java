package org.example.models.muscles;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Entity
@ToString
public class Muscle {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;

    @Lob
    private String info;

    private String image;

    @ManyToOne
    private MuscleGroup muscleGroup;

}
