package com.tosDev.tg.bot.enums;

public enum ShiftStatusEnum {
    AT_WORK("В работе"),
    FINISHED("Работа окончена"),
    APPROVED("Одобрено менеджером");

    private final String description;

    ShiftStatusEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
