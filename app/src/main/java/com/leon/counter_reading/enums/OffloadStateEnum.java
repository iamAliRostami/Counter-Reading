package com.leon.counter_reading.enums;

public enum OffloadStateEnum {
    INSERTED(8),
    SENT(9),
    REGISTERED(10),
    SENT_WITH_ERROR(11),
    ARCHIVED(12),
    LOGICAL_DELETED(16),
    DELETED(32);

    private final int value;

    OffloadStateEnum(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
