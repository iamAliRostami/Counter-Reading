package com.leon.counter_reading.tables;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ImageGrouped {
    public final ArrayList<MultipartBody.Part> File;
    public RequestBody OnOffLoadId;
    public RequestBody Description;

    public ImageGrouped() {
        File = new ArrayList<>();
    }
}
