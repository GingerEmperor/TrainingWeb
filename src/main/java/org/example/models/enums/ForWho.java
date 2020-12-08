package org.example.models.enums;

import lombok.Getter;

@Getter
public enum ForWho {
    BEGINNER("Новичок","#448d87"),
    // AMATEUR_ATHLETE("Любитель"),
    ADVANCED("Продвинутый","#4d74a0"),
    PROFESSIONAL("Професионал","#c95f6c");
    // MONSTER("Монстр");

    String word;
    String colorCode;

    ForWho(final String word,final String colorCode) {
        this.word = word;
        this.colorCode=colorCode;
    }
}
