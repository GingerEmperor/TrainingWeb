package org.example.utill.alerts;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class Alert {

    private static final String ALERT_CLASS_FORMAT = "alert alert-%s alert-dismissible fade show";

    private final AlertType type;

    private final String description;

    enum AlertType {
        PRIMARY("primary"),
        SECONDARY("secondary"),
        SUCCESS("success"),
        DANGER("danger"),
        WARNING("warning"),
        INFO("info"),
        LIGHT("light"),
        DARK("dark");

        private final String typeString;

        private final String alertClass;

        AlertType(String typeString) {
            this.typeString = typeString;
            this.alertClass = String.format(ALERT_CLASS_FORMAT, typeString);
        }
    }

    public String getAlertTypeString() {
        return type.typeString;
    }

    public String getAlertClassString() {
        return type.alertClass;
    }
}
