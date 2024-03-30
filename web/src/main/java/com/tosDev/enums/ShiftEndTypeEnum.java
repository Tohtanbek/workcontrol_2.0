package com.tosDev.enums;

public enum ShiftEndTypeEnum {
    PLANNED("Плановое"),
    UNPLANNED("Неплановое");

    private final String description;

    ShiftEndTypeEnum(String description){
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
