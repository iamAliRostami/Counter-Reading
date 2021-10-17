package com.leon.counter_reading.tables;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "ReadingConfigDefaultDto", indices = @Index(value = {"customId"/*,"id","zoneId"*/}, unique = true))
public class ReadingConfigDefaultDto {
    @PrimaryKey(autoGenerate = true)
    public int customId;
    public String id;
    public int zoneId;
    public int defaultAlalHesab;
    public int maxAlalHesab;
    public int minAlalHesab;
    public int defaultImagePercent;
    public int maxImagePercent;
    public int minImagePercent;
    public boolean defaultHasPreNumber;
    public boolean isOnQeraatCode;
    public boolean displayBillId;
    public boolean displayRadif;
    public int lowConstBoundMaskooni;
    public int lowPercentBoundMaskooni;
    public int highConstBoundMaskooni;
    public int highPercentBoundMaskooni;
    public int lowConstBoundSaxt;
    public int lowPercentBoundSaxt;
    public int highConstBoundSaxt;
    public int highPercentBoundSaxt;
    public int lowConstZarfiatBound;
    public int lowPercentZarfiatBound;
    public int highConstZarfiatBound;
    public int highPercentZarfiatBound;
    public int lowPercentRateBoundNonMaskooni;
    public int highPercentRateBoundNonMaskooni;
    public boolean isActive;
    public boolean isArchive;
    public String zone;

}
