package org.example.models.enums;

import lombok.Getter;

@Getter
public enum Goal {
    STRENGTH("Сила","#1e8ca1"),
    FAT_BURNING("Жиросжигание","#fccc40");

    String word;
    String colorCode;

    Goal(final String word, final String colorCode) {
        this.word=word;
        this.colorCode=colorCode;
    }
}
