package com.tosDev.enums;

public enum AssignmentStatus {
    DRAFT ("Черновик"),
    AT_WORK ("Выдано работнику"),
    READY ("Отработано"),
    CLOSED ("Рассчитано");

    private final String description;
    AssignmentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
