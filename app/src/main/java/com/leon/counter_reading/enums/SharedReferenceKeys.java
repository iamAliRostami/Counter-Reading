package com.leon.counter_reading.enums;

public enum SharedReferenceKeys {
    READING_REPORT("reading_report"),
    ACCOUNT("account"),
    IMAGE("image"),
    MOBILE("mobile"),
    SERIAL("serial"),
    ADDRESS("address"),
    AHAD_EMPTY("ahad_empty"),
    AHAD_1("ahad_1"),
    AHAD_2("ahad_2"),
    AHAD_TOTAL("ahad_total"),
    KARBARI("karbari"),
    DESCRIPTION("description"),
    SHOW_AHAD_TITLE("show_ahad_title"),
    USERNAME_TEMP("username_temp"),
    PASSWORD_TEMP("password_temp"),
    USERNAME("username"),
    PASSWORD("password"),
    TOKEN("token"),
    REFRESH_TOKEN("refresh_token"),
    LOAD_USER_PASSWORD("load_user_password"),
    ANTIFORGERY("Antiforgery"),
    XSRF("xsrf"),
    USER_CODE("user_code"),
    DISPLAY_NAME("display_name"),
    THEME_STABLE("theme_stable"),
    DATE("date"),
    POINT("point"),
    SORT_TYPE("sort_type"),
    PERSONAL_CODE("personal_number"),
    AVATAR("avatar"),
    THEME_TEMPORARY("theme_temporary");

    private final String value;

    SharedReferenceKeys(final String newValue) {
        value = newValue;
    }

    public String getValue() {
        return value;
    }
}
