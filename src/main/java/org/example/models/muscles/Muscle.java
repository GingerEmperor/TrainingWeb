package org.example.models.muscles;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

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

    private String image;

    @ManyToOne
    private MuscleGroup muscleGroup;

    // public MuscleGroup getMuscleGroup() {
    //     return muscleGroup;
    // }
    //
    // public void setMuscleGroup(MuscleGroup muscleGroup) {
    //     this.muscleGroup = muscleGroup;
    // }
    //
    //
    // public String getInfo() {
    //     return info;
    // }
    //
    // public void setInfo(String info) {
    //     this.info = info;
    // }
    //
    //
    // public Long getId() {
    //     return id;
    // }
    //
    // public void setId(Long id) {
    //     this.id = id;
    // }
    //
    // public String getName() {
    //     return name;
    // }
    //
    // public void setName(String name) {
    //     this.name = name;
    // }
    //
    // public String getImage() {
    //     return image;
    // }
    //
    // public void setImage(final String image) {
    //     this.image = image;
    // }
}
