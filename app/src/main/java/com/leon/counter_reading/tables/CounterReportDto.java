package com.leon.counter_reading.tables;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "CounterReportDto", indices = @Index(value = "customId", unique = true))
public class CounterReportDto {
    @PrimaryKey(autoGenerate = true)
    public int customId;
    public int id;
    public int moshtarakinId;
    public String title;
    public int zoneId;
    public boolean isAhad;
    public boolean isKarbari;
    public boolean canNumberBeLessThanPre;
    public boolean isTavizi;
    public int clientOrder;

    @Ignore
    public boolean isSelected;
}
