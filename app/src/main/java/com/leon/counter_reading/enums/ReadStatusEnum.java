package com.leon.counter_reading.enums;

public enum ReadStatusEnum {
    ALL(0),
    UNREAD(1),
    ALL_MANE(2),
    CUSTOM_MANE(3),
    STATE(4),
    TRACK_NUMBER(5),
    READ(6),
    ALL_MANE_UNREAD(7);

    private final int value;

    ReadStatusEnum(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
