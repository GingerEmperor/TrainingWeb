package org.example.models.enums;

public enum Equipment {
    OWN_WEIGHT("СобственыйВес"),     //свой вес
    SPORT_GROUND("СпортПлощадка"),   //спорт площадка
    DUMBBELLS("Гантели"),      //гантели
    BARBELL("Штанга"),        //штанга
    TRAINING_APPARATUS("Тренажёр"); // тренажер

    String word;

    Equipment(final String word) {
        this.word = word;
    };

    public String getWord() {
        return word;
    }

    // @Override
    // public String toString() {
    //     return word;
    // }
}
