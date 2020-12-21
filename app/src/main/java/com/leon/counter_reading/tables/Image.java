package com.leon.counter_reading.tables;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

import okhttp3.MultipartBody;

@Entity(tableName = "Image", indices = @Index(value = "id", unique = true))
public class Image {
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


    public static class ImageGrouped {
        public String OnOffLoadId;
        public String Description;
        public ArrayList<MultipartBody.Part> File;

        public ImageGrouped() {
            File = new ArrayList<>();
        }
    }

    public static class ImageMultiple {
        public ArrayList<String> OnOffLoadId;
        public ArrayList<String> Description;
        public ArrayList<String> File;

        public ImageMultiple() {
            File = new ArrayList<>();
            Description = new ArrayList<>();
            OnOffLoadId = new ArrayList<>();
        }
    }
}
