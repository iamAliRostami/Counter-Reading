package com.leon.counter_reading.tables;

public class PasswordInfo {
    public String oldPassword;
    public String newPassword;
    public String confirmPassword;

    public PasswordInfo(String oldPassword, String newPassword, String confirmPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }
}
