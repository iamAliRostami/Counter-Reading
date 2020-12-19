package com.leon.counter_reading.enums;

/**
 * Created by saeid on 2/16/2017.
 */
public enum CompanyNames {
    ZONE1(1),
    ZONE2(2),
    ZONE3(3),
    ZONE4(4),
    ZONE5(5),
    ZONE6(6),
    TSW(7),
    TE(8),
    TSE(9),
    TOWNS_WEST(10),
    ESF(11),
    DEBUG(12),
    ESF_MAP(13);

    private final int value;

    CompanyNames(final int newValue) {
        value = newValue;
    }

    public int getValue() {
        return value;
    }

}
