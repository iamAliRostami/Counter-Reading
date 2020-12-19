package com.leon.counter_reading.enums;

public enum MultimediaTypeEnum {
    AUDIO("1"),
    IMAGE("2"),
    VIDEO("3"),
    TEXT("4");
    private final String value;

    MultimediaTypeEnum(final String newValue) {
        value = newValue;
    }

    public String getValue() {
        return value;
    }
}
