package com.leon.counter_reading.tables;

public class LoginFeedBack {
    public final String access_token;
    public final String refresh_token;
    public String displayName;
    public String userCode;
    public String XSRFToken;
    public String message;
    public int status;
    public boolean isValid;

    public LoginFeedBack(String access_token, String refresh_token) {
        this.access_token = access_token;
        this.refresh_token = refresh_token;
    }
}
