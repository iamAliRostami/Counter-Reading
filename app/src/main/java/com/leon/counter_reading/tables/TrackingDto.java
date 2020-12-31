package com.leon.counter_reading.tables;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "TrackingDto", indices = @Index(value = {"customId"/*,"id","trackNumber"*/}, unique = true))
public class TrackingDto {
    @PrimaryKey(autoGenerate = true)
    public int customId;
    public String id;
    public int trackNumber;
    public String listNumber;
    public String insertDateJalali;
    public int zoneId;
    public String zoneTitle;
    public boolean isBazdid;
    public int year;
    public boolean isRoosta;
    public String fromEshterak;
    public String toEshterak;
    public String fromDate;
    public String toDate;
    public int itemQuantity;
    public int alalHesabPercent;
    public int imagePercent;
    public boolean hasPreNumber;
    public boolean displayBillId;
    public boolean displayRadif;

    public boolean isActive;
    public boolean isArchive;
}