package org.example.models.criteria;

import org.example.models.enums.Equipment;

public class EquipmentCriteria implements Crit {
    private Equipment equipment;

    public EquipmentCriteria(final Equipment equipment) {
        this.equipment = equipment;
    }

    @Override
    public String toString() {
        return equipment.getWord();
    }
}
