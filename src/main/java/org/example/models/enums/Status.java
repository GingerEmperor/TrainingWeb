package org.example.models.enums;

import lombok.Getter;

@Getter
public enum Status {
    ACTIVE("#5cb85c"),
    BANNED("#d9534f"),
    DELETED("#292b2c");

    String colorCode;

    Status(final String colorCode) {
        this.colorCode = colorCode;
    }
}
