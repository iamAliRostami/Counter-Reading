package com.leon.counter_reading.tables;

import androidx.room.Entity;
import androidx.room.Ignore;
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
    @Ignore
    public String qotr;
    public int sifoonQotrCode;
    @Ignore
    public String sifoonQotr;

    @Ignore
    public boolean hasPreNumber;
    @Ignore
    public boolean displayBillId;
    @Ignore
    public boolean displayRadif;

    public String postalCode;
    public int preNumber;
    public String preDate;
    public String preDateMiladi;
    public double preAverage;
    public int preCounterStateCode;//TODO join counter state is xarab
    public String counterSerial;
    public String counterInstallDate;
    public String tavizDate;
    public String tavizNumber;
    public String trackingId;
    public int trackNumber;
    public int zarfiat;
    public String mobile;
    public int hazf;//TODO 0 <  hazf movaqat
    public int noeVagozariId;//TODO 4: sax o saz or karbari isSaxt
    public Integer counterNumber;
    public int counterStateId;
    public String possibleAddress;
    public String possibleCounterSerial;
    public String possibleEshterak;
    public String possibleMobile;
    public String possiblePhoneNumber;
    public int possibleAhadMaskooniOrAsli;
    public int possibleAhadTejariOrFari;
    public int possibleAhadSaierOrAbBaha;
    public int possibleEmpty;
    public int possibleKarbariCode;
    public String description;
//    @Ignore
    public String phoneDateTime;
//    @Ignore
    public String locationDateTime;
    //TODO
    public String d1;
    public String d2;
    public int offLoadStateId;
    public int zoneId;
    public double gisAccuracy;
    public double x;
    public double y;
    public boolean counterNumberShown;

    public int attemptCount;
    public boolean isLocked;

    public int highLowStateId;
    public boolean isBazdid;
    public Integer counterStatePosition;


    public static class OffLoad {
        public String id;
        public Integer counterNumber;
        public int counterStateId;
        public String possibleAddress;
        public String possibleCounterSerial;
        public String possibleEshterak;
        public String possibleMobile;
        public String possiblePhoneNumber;
        public int possibleAhadMaskooniOrAsli;
        public int possibleAhadTejariOrFari;
        public int possibleAhadSaierOrAbBaha;
        public int possibleEmpty;
        public int possibleKarbariCode;
        public String description;
        public boolean counterNumberShown;
        public boolean isLocked;
        public double gisAccuracy;
        public double x;
        public double y;
        public String d1;
        public String d2;
        public int attemptCount;
        public String phoneDateTime;
        public String locationDateTime;

        public OffLoad() {
        }

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
            possibleEmpty = onOffLoadDto.possibleEmpty;
            possibleKarbariCode = onOffLoadDto.possibleKarbariCode;
            description = onOffLoadDto.description;
            counterNumberShown = onOffLoadDto.counterNumberShown;
            x = onOffLoadDto.x;
            y = onOffLoadDto.y;
            d1 = onOffLoadDto.d1;
            d2 = onOffLoadDto.d2;
            gisAccuracy = onOffLoadDto.gisAccuracy;
            attemptCount = onOffLoadDto.attemptCount;
            isLocked = onOffLoadDto.isLocked;

            phoneDateTime = onOffLoadDto.phoneDateTime;
            locationDateTime = onOffLoadDto.locationDateTime;
        }
    }

    public static class OffLoadData {
        public final ArrayList<OffLoadReport> offLoadReports;
        public boolean isFinal;//TODO upload or reading
        public int finalTrackNumber;
        public ArrayList<OffLoad> offLoads;

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
        public String[] targetObject;
    }
}
