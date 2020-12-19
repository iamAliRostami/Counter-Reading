package com.leon.counter_reading.enums;

public enum ImageScale {
    AS_IS(1),
    HALF_SMALL(2),
    SMALL(3),
    X_SMALL(4),
    XX_SMALL(8),
    TINY(16),
    SUPER_TINY(32);

    private final int value;

    ImageScale(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
