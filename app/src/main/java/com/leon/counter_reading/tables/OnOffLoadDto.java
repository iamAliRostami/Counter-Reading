package com.leon.counter_reading.tables;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "OnOffLoadDto", indices = @Index(value = {"customId"/*,"id"*/}, unique = true))
public class OnOffLoadDto {
    @PrimaryKey(autoGenerate = true)
    public int customId;
    public String id;
    public String billId;
    public int radif;
    public String eshterak;
    public String qeraatCode;
    public String firstName;
    public String sureName;
    public String address;
    public String pelak;
    public int karbariCode;
    public int ahadMaskooniOrAsli;
    public int ahadTejariOrFari;
    public int ahadSaierOrAbBaha;
    public int qotrCode;
    public int sifoonQotrCode;
    public String postalCode;
    public int preNumber;
    public String preDate;
    public String preDateMiladi;
    public double preAverage;
    public int preCounterStateCode;
    public String counterSerial;
    public String counterInstallDate;
    public String tavizDate;
    public String tavizNumber;
    public String trackingId;
    public int zarfiat;
    public String mobile;
    public int hazf;
    public int noeVagozariId;
    public int counterNumber;
    public int counterStateId;
    public String possibleAddress;
    public String possibleCounterSerial;
    public String possibleEshterak;
    public String possibleMobile;
    public String possiblePhoneNumber;
    public String possibleAhadMaskooniOrAsli;
    public String possibleAhadTejariOrFari;
    public String possibleAhadSaierOrAbBaha;
    public String possibleKarbariCode;
    public String description;
    public int zoneId;
    //TODO
    public int offLoadStateId;
    public int highLowStateId;
    public boolean isBazdid;
    public Integer counterStatePosition;
}
