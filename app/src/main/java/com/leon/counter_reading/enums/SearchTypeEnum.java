package com.leon.counter_reading.enums;

public enum SearchTypeEnum {
    ESHTERAK(0),
    RADIF(1),
    BODY_COUNTER(2),
    NAME(3),
    PAGE_NUMBER(4),
    BARCODE(5),
    All(6);

    private final int value;

    SearchTypeEnum(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }
}
