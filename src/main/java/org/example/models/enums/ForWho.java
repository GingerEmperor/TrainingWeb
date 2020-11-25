package org.example.models.enums;

import lombok.Data;
import lombok.Getter;

@Getter
public enum ForWho {
    BEGINNER("Новичок"),
    // AMATEUR_ATHLETE("Любитель"),
    ADVANCED("Продвинутый"),
    PROFESSIONAL("Професионал");
    // MONSTER("Монстр");

    String world;

    ForWho(final String word) {
        this.world = word;
    }
}
