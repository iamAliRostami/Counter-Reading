package com.leon.counter_reading.enums;

public enum ProgressType {
    SHOW(1),
    SHOW_CANCELABLE(2),
    SHOW_CANCELABLE_REDIRECT(4),
    NOT_SHOW(3);

    private final int value;

    ProgressType(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
