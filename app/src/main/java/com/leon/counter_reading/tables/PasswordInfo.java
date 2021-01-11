package com.leon.counter_reading.tables;

public class PasswordInfo {
    public final String oldPassword;
    public final String newPassword;
    public final String confirmPassword;

    public PasswordInfo(String oldPassword, String newPassword, String confirmPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }
}
