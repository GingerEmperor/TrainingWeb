package org.example.models;

import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "exercise")
@Entity
public class ExerciseInfo {

    public ExerciseInfo(final Exercise exercise) {
        this.exercise = exercise;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Lob
    private String someInfo;
    @Lob
    private String howToDo;
    private String videoLink;

    @OneToOne()
    @JoinColumn(name = "exercise_id",referencedColumnName = "id")
    private Exercise exercise;

}
