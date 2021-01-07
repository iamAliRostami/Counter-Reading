package com.leon.counter_reading.tables;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@Entity(tableName = "Voice", indices = @Index(value = "id", unique = true))
public class Voice {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String OnOffLoadId;
    public String Description;
    public String address;
    public boolean isSent;
    public boolean isDeleted;
    public boolean isArchived;
    @Ignore
    public MultipartBody.Part File;


    public static class VoiceGrouped {
        public RequestBody OnOffLoadId;
        public RequestBody Description;
        public ArrayList<MultipartBody.Part> File;

        public VoiceGrouped() {
            File = new ArrayList<>();
        }
    }

    public static class VoiceMultiple {
        public ArrayList<RequestBody> OnOffLoadId;
        public ArrayList<RequestBody> Description;
        public ArrayList<MultipartBody.Part> File;

        public VoiceMultiple() {
            File = new ArrayList<>();
            Description = new ArrayList<>();
            OnOffLoadId = new ArrayList<>();
        }
    }

    public static class VoiceUploadResponse {
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
