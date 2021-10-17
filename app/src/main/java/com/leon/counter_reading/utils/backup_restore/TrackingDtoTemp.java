package com.leon.counter_reading.utils.backup_restore;

import com.leon.counter_reading.tables.TrackingDto;

public class TrackingDtoTemp {
    public String id;
    public String listNumber;
    public String insertDateJalali;
    public String zoneTitle;
    public String fromEshterak;
    public String toEshterak;
    public String fromDate;
    public String toDate;

    public int trackNumber;
    public int zoneId;
    public int year;
    public int itemQuantity;
    public int alalHesabPercent;
    public int imagePercent;

    public int isRoosta;
    public int isBazdid;
    public int hasPreNumber;
    public int displayBillId;
    public int displayRadif;
    public int isActive;
    public int isArchive;
    public int isLocked;

    public TrackingDto getTrackingDto() {
        TrackingDto trackingDto = new TrackingDto();
        trackingDto.id = id;
        trackingDto.listNumber = listNumber;
        trackingDto.insertDateJalali = insertDateJalali;
        trackingDto.zoneTitle = zoneTitle;
        trackingDto.fromEshterak = fromEshterak;
        trackingDto.toEshterak = toEshterak;
        trackingDto.fromDate = fromDate;
        trackingDto.toDate = toDate;

        trackingDto.trackNumber = trackNumber;
        trackingDto.zoneId = zoneId;
        trackingDto.year = year;
        trackingDto.itemQuantity = itemQuantity;
        trackingDto.alalHesabPercent = alalHesabPercent;
        trackingDto.imagePercent = imagePercent;

        trackingDto.isRoosta = isRoosta == 1;
        trackingDto.isBazdid = isBazdid == 1;
        trackingDto.hasPreNumber = hasPreNumber == 1;
        trackingDto.displayBillId = displayBillId == 1;
        trackingDto.displayRadif = displayRadif == 1;
        trackingDto.isActive = isActive == 1;
        trackingDto.isArchive = isArchive == 1;
        trackingDto.isLocked = isLocked == 1;

        return trackingDto;
    }
}