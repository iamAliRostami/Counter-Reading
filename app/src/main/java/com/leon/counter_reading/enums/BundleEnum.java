package com.leon.counter_reading.enums;

public enum BundleEnum {
    BILL_ID("bill_Id"),
    THEME("theme"),
    ON_OFF_LOAD("on_off_load"),
    READING_CONFIG("reading_config"),
    KARBARI_DICTONARY("karbari_dictionary"),
    QOTR_DICTIONARY("qotr_dictionary"),
    COUNTER_STATE("counter_State"),
    COUNTER_STATE_ADAPTER("counter_State_adapter"),
    COUNTER_STATE_POSITION("counter_state_position"),
    COUNTER_STATE_CODE("counter_state_code"),
    TRACKING("tracking"),
    POSITION("position"),
    NUMBER("number"),
    UNREAD("total"),
    TOTAL("unread"),
    ZERO("zero"),
    HIGH("high"),
    LOW("low"),
    Item("item"),
    NORMAL("normal"),
    READ_STATUS("read_status"),
    TYPE("type");

    private final String value;

    BundleEnum(final String newValue) {
        value = newValue;
    }

    public String getValue() {
        return value;
    }
}
