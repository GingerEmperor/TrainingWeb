package org.example.models.enums;

import lombok.Getter;

@Getter
public enum Goal {
    STRENGTH("Сила"),
    FAT_BURNING("Жиросжигание");

    String word;

    Goal(final String word) {
        this.word=word;
    }
}
