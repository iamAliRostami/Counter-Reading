package com.leon.counter_reading.tables;

public class LoginInfo {
    public final String username;
    public final String password;
    public final String deviceSerial;

    public LoginInfo(String username, String password, String deviceSerial) {
        this.username = username;
        this.password = password;
        this.deviceSerial = deviceSerial;
    }
}
