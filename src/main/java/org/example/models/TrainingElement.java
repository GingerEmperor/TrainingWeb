package org.example.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class TrainingElement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Exercise exercise;

    private int howMuchToDo;

    private int recommendedTimeToDoLessThan;

    private int timeToRest;

    @Override
    public String toString() {
        return "TrainingElement{" +
                "id=" + id +
                ", exercise=" + exercise.getTitle() +
                ", howMuchToDo=" + howMuchToDo +
                ", recommendedTimeToDoLessThan=" + recommendedTimeToDoLessThan +
                ", timeToRest=" + timeToRest +
                '}';
    }
}
