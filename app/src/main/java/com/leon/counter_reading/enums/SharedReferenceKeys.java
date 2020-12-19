package com.leon.counter_reading.enums;

public enum SharedReferenceKeys {
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
    THEME_TEMPORARY("theme_temporary");

    private final String value;

    SharedReferenceKeys(final String newValue) {
        value = newValue;
    }

    public String getValue() {
        return value;
    }
}
