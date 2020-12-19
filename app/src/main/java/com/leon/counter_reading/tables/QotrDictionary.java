package com.leon.counter_reading.tables;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "QotrDictionary", indices = @Index(value = {"customId"/*, "id"*/}, unique = true))
public class QotrDictionary {
    @PrimaryKey(autoGenerate = true)
    public int customId;
    public int id;
    public String title;
    public boolean isSelected;
}