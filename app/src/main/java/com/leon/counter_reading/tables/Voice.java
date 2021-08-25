package com.leon.counter_reading.tables;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import okhttp3.MultipartBody;

@Entity(tableName = "Voice", indices = @Index(value = "id", unique = true))
public class Voice {
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
}
