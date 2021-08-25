package com.leon.counter_reading.tables;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ImageMultiple {
    public final ArrayList<RequestBody> OnOffLoadId;
    public final ArrayList<RequestBody> Description;
    public final ArrayList<MultipartBody.Part> File;

    public ImageMultiple() {
        File = new ArrayList<>();
        Description = new ArrayList<>();
        OnOffLoadId = new ArrayList<>();
    }
}
