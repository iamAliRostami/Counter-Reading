package com.leon.counter_reading.tables;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "OnOffLoadDto", indices = @Index(value = {"customId"}, unique = true))
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
    public int possibleAhadMaskooniOrAsli;
    public int possibleAhadTejariOrFari;
    public int possibleAhadSaierOrAbBaha;
    public int possibleKarbariCode;
    public String description;
    //TODO
    public int offLoadStateId;
    public int zoneId;
    public double gisAccuracy;
    public double x;
    public double y;
    public boolean counterNumberShown;

    public int highLowStateId;
    public boolean isBazdid;
    public Integer counterStatePosition;


    public static class OffLoad {
        public String id;
        public int counterNumber;
        public int counterStateId;
        public String possibleAddress;
        public String possibleCounterSerial;
        public String possibleEshterak;
        public String possibleMobile;
        public String possiblePhoneNumber;
        public int possibleAhadMaskooniOrAsli;
        public int possibleAhadTejariOrFari;
        public int possibleAhadSaierOrAbBaha;
        public int possibleKarbariCode;
        public String description;
        public boolean counterNumberShown;
        public double gisAccuracy;
        public double x;
        public double y;

        public OffLoad(OnOffLoadDto onOffLoadDto) {
            id = onOffLoadDto.id;
            counterNumber = onOffLoadDto.counterNumber;//TODO
            counterStateId = onOffLoadDto.counterStateId;
            possibleAddress = onOffLoadDto.possibleAddress;
            possibleCounterSerial = onOffLoadDto.possibleCounterSerial;
            possibleEshterak = onOffLoadDto.possibleEshterak;
            possibleMobile = onOffLoadDto.possibleMobile;
            possiblePhoneNumber = onOffLoadDto.possiblePhoneNumber;
            possibleAhadMaskooniOrAsli = onOffLoadDto.possibleAhadMaskooniOrAsli;
            possibleAhadSaierOrAbBaha = onOffLoadDto.possibleAhadSaierOrAbBaha;
            possibleAhadTejariOrFari = onOffLoadDto.possibleAhadTejariOrFari;
            possibleKarbariCode = onOffLoadDto.possibleKarbariCode;
            description = onOffLoadDto.description;
            counterNumberShown = onOffLoadDto.counterNumberShown;
            x = onOffLoadDto.x;
            y = onOffLoadDto.y;
            gisAccuracy = onOffLoadDto.gisAccuracy;
        }
    }

    public static class OffLoadData {
        public boolean isFinal;//TODO upload or reading
        public ArrayList<OffLoad> offLoads;
        public ArrayList<OffLoadReport> offLoadReports;

        public OffLoadData() {
            offLoadReports = new ArrayList<>();
            offLoads = new ArrayList<>();
        }
    }

    public static class OffLoadResponses {
        public int status;
        public String message;
        public String generationDateTime;
        public boolean isValid;
        public ArrayList<String> targetObject;
    }
}
