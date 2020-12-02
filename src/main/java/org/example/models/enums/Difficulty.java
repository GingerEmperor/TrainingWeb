package org.example.models.enums;

import lombok.Getter;

@Getter
public enum  Difficulty {
    EASY("Легко","#6eaa10"),
    NORMAL("Средне","#f5e055"),
    HARD("Сложно","#aa4237");

    String word;
    String colorCode;

    Difficulty(final String word, final String colorCode) {
        this.word=word;
        this.colorCode=colorCode;
    }
}
