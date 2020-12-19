package com.leon.counter_reading.tables;

import com.google.gson.annotations.SerializedName;

public class SimpleMessage {
    @SerializedName("message")
    String message;

    public SimpleMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
