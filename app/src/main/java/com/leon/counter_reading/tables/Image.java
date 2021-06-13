package com.leon.counter_reading.tables;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@Entity(tableName = "Image", indices = @Index(value = "id", unique = true))
public class Image {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String OnOffLoadId;
    public int trackNumber;
    public String Description;
    public String address;
    public boolean isSent;
    public boolean isDeleted;
    public boolean isArchived;
    @Ignore
    public MultipartBody.Part File;


    public static class ImageGrouped {
        public RequestBody OnOffLoadId;
        public RequestBody Description;
        public final ArrayList<MultipartBody.Part> File;

        public ImageGrouped() {
            File = new ArrayList<>();
        }
    }

    public static class ImageMultiple {
        public final ArrayList<RequestBody> OnOffLoadId;
        public final ArrayList<RequestBody> Description;
        public final ArrayList<MultipartBody.Part> File;

        public ImageMultiple() {
            File = new ArrayList<>();
            Description = new ArrayList<>();
            OnOffLoadId = new ArrayList<>();
        }
    }

    public static class ImageUploadResponse {
        public int status;
        public Errors errors;
        public String message;
        public String generationDateTime;
        public boolean isValid;
        public Object targetObject;
        public String type;
        public String title;
        public String traceId;
    }

    public static class Errors {
        public ArrayList<String> onOffLoadId;
    }
}
