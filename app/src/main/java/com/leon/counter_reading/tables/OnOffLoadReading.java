package com.leon.counter_reading.tables;

import java.util.ArrayList;

public class OnOffLoadReading {
    public String id;
    public String billId;
    public int radif;
    public String eshterak;
    public String qeraatCode;
    public String firstName;
    public String sureName;
    public String address;
    public int karbariCode;
    public String karbari;
    public int ahadMaskooniOrAsli;
    public int ahadTejariOrFari;
    public int ahadSaierOrAbBaha;
    public int qotrCode;
    public String qotr;
    public int sifoonQotrCode;
    public String sifoonQotr;
    public String postalCode;
    public int preNumber;
    public String preDate;
    public double preAverage;
    public int preCounterStateCode;
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
    public int possibleEmpty;
    public int possibleKarbariCode;
    public String description;
    //TODO
    public String d1;
    public String d2;
    public int offLoadStateId;
    public int zoneId;
    public double gisAccuracy;
    public double x;
    public double y;
    public boolean counterNumberShown;

    public boolean hasPreNumber;
    public boolean displayBillId;
    public boolean displayRadif;

    public int attemptNumber;
    public boolean isLocked;

    public int highLowStateId;
    public boolean isBazdid;
    public Integer counterStatePosition;


    public static class OffLoad {
        public final String id;
        public final int counterNumber;
        public final int counterStateId;
        public final String possibleAddress;
        public final String possibleCounterSerial;
        public final String possibleEshterak;
        public final String possibleMobile;
        public final String possiblePhoneNumber;
        public final int possibleAhadMaskooniOrAsli;
        public final int possibleAhadTejariOrFari;
        public final int possibleAhadSaierOrAbBaha;
        public final int possibleEmpty;
        public final int possibleKarbariCode;
        public final String description;
        public final boolean counterNumberShown;
        public final double gisAccuracy;
        public final double x;
        public final double y;
        public final String d1;
        public final String d2;

        public OffLoad(OnOffLoadReading onOffLoadDto) {
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
        }
    }

    public static class OffLoadData {
        public final ArrayList<OffLoad> offLoads;
        public final ArrayList<OffLoadReport> offLoadReports;
        public boolean isFinal;//TODO upload or reading
        public int finalTrackNumber;

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
