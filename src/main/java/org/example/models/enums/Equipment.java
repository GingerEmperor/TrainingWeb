package org.example.models.enums;

import lombok.Getter;

@Getter
public enum Equipment {
    OWN_WEIGHT("СобственыйВес","#5cb85c"),     //свой вес
    SPORT_GROUND("СпортПлощадка","#d9534f"),   //спорт площадка
    DUMBBELLS("Гантели","#f0ad4e"),      //гантели
    BARBELL("Штанга","#5bc0de"),        //штанга
    TRAINING_APPARATUS("Тренажёр","#0275d8"); // тренажер

    String word;
    String colorCode;

    // Equipment(final String word) {
    //     this.word = word;
    // };
    Equipment(final String word,final String colorCode) {
        this.word = word;
        this.colorCode=colorCode;
    };


    // @Override
    // public String toString() {
    //     return word;
    // }
}
