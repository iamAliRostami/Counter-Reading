package com.leon.counter_reading.tables;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "KarbariDto", indices = @Index(value = {"customId"/*,"id","moshtarakinId"*/}, unique = true))
public class KarbariDto {
    @PrimaryKey(autoGenerate = true)
    public int customId;
    public int id;
    public int moshtarakinId;
    public String title;
    public int provinceId;
    public boolean isMaskooni;
    public boolean isSaxt;
    public boolean hasReadingVibrate;
    public boolean isTejari;
}