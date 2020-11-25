package org.example.models.enums;

import lombok.Getter;

@Getter
public enum  Difficulty {
    EASY("Легко"),
    NORMAL("Средне"),
    HARD("Сложно");

    String word;

    Difficulty(final String word) {
        this.word=word;
    }
}
