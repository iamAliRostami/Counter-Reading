package com.leon.counter_reading.tables;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "CounterStateDto", indices = @Index(value = {"customId"/*,"id","moshtarakinId"*/}, unique = true))
public class CounterStateDto {
    @PrimaryKey(autoGenerate = true)
    public int customId;
    public int id;
    public int moshtarakinId;
    public String title;
    public int zoneId;
    public int clientOrder;
    public boolean canEnterNumber;
    public boolean isMane;
    public boolean canNumberBeLessThanPre;
    public boolean isTavizi;
    public boolean shouldEnterNumber;
    public boolean isXarab;
    public boolean isFaqed;
}