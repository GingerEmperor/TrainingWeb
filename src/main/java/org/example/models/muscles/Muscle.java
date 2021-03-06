package org.example.models.muscles;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Entity
public class Muscle {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Lob
    private String info;

    @Lob
    private String functions;

    private String image;

    @ManyToOne
    @JoinColumn(name = "muscle_group_id", referencedColumnName = "id")
    private MuscleGroup muscleGroup;

    @Override
    public String toString() {
        return getName();
    }
}
