package com.leon.counter_reading.utils.backup_restore;

import static com.leon.counter_reading.utils.backup_restore.Restore.getIntFromString;

import com.leon.counter_reading.tables.OnOffLoadDto;

public class OnOffLoadDtoTemp {
    public String id;
    public String billId;
    public String eshterak;
    public String qeraatCode;
    public String firstName;
    public String sureName;
    public String address;
    public String pelak;
    public String qotr;
    public String sifoonQotr;
    public String postalCode;
    public String preDate;
    public String preDateMiladi;
    public String counterSerial;
    public String counterInstallDate;
    public String tavizDate;
    public String tavizNumber;
    public String trackingId;
    public String possibleAddress;
    public String possibleCounterSerial;
    public String possibleEshterak;
    public String possibleMobile;
    public String possiblePhoneNumber;
    public String description;
    public String d1;
    public String d2;
    public String mobile;
    //
    public int radif;
    public int karbariCode;
    public int ahadMaskooniOrAsli;
    public int ahadTejariOrFari;
    public int ahadSaierOrAbBaha;
    public int qotrCode;
    public int sifoonQotrCode;
    public int preNumber;
    public int preCounterStateCode;
    public int trackNumber;
    public int zarfiat;
    public int hazf;
    public int noeVagozariId;
    public int counterStateId;
    public int possibleAhadMaskooniOrAsli;
    public int possibleAhadTejariOrFari;
    public int possibleAhadSaierOrAbBaha;
    public int possibleEmpty;
    public int possibleKarbariCode;
    public int offLoadStateId;
    public int zoneId;
    public int attemptCount;
    public int highLowStateId;

    public double gisAccuracy;
    public double preAverage;
    public double x;
    public double y;

    public String counterNumber;
    public String counterStatePosition;

    public int counterNumberShown;
    public int hasPreNumber;
    public int displayBillId;
    public int displayRadif;
    public int isLocked;
    public int isBazdid;

    public OnOffLoadDto getOnOffLoadDto() {
        OnOffLoadDto onOffLoadDto = new OnOffLoadDto();
        onOffLoadDto.hasPreNumber = hasPreNumber == 1;
        onOffLoadDto.displayBillId = displayBillId == 1;
        onOffLoadDto.displayRadif = displayRadif == 1;
        onOffLoadDto.isLocked = isLocked == 1;
        onOffLoadDto.isBazdid = isBazdid == 1;
        onOffLoadDto.counterNumberShown = counterNumberShown == 1;

        onOffLoadDto.id = id;
        onOffLoadDto.billId = billId;
        onOffLoadDto.eshterak = eshterak;
        onOffLoadDto.qeraatCode = qeraatCode;
        onOffLoadDto.firstName = firstName;
        onOffLoadDto.sureName = sureName;
        onOffLoadDto.address = address;
        onOffLoadDto.pelak = pelak;
        onOffLoadDto.qotr = qotr;
        onOffLoadDto.sifoonQotr = sifoonQotr;
        onOffLoadDto.postalCode = postalCode;
        onOffLoadDto.preDate = preDate;
        onOffLoadDto.preDateMiladi = preDateMiladi;
        onOffLoadDto.counterSerial = counterSerial;
        onOffLoadDto.counterInstallDate = counterInstallDate;
        onOffLoadDto.tavizDate = tavizDate;
        onOffLoadDto.tavizNumber = tavizNumber;
        onOffLoadDto.trackingId = trackingId;
        onOffLoadDto.possibleAddress = possibleAddress;
        onOffLoadDto.possibleCounterSerial = possibleCounterSerial;
        onOffLoadDto.possibleEshterak = possibleEshterak;
        onOffLoadDto.possibleMobile = possibleMobile;
        onOffLoadDto.possiblePhoneNumber = possiblePhoneNumber;
        onOffLoadDto.description = description;
        onOffLoadDto.d1 = d1;
        onOffLoadDto.d2 = d2;
        onOffLoadDto.mobile = mobile;
//
        onOffLoadDto.radif = radif;
        onOffLoadDto.karbariCode = karbariCode;
        onOffLoadDto.ahadMaskooniOrAsli = ahadMaskooniOrAsli;
        onOffLoadDto.ahadTejariOrFari = ahadTejariOrFari;
        onOffLoadDto.ahadSaierOrAbBaha = ahadSaierOrAbBaha;
        onOffLoadDto.qotrCode = qotrCode;
        onOffLoadDto.sifoonQotrCode = sifoonQotrCode;
        onOffLoadDto.preNumber = preNumber;
        onOffLoadDto.preCounterStateCode = preCounterStateCode;
        onOffLoadDto.trackNumber = trackNumber;
        onOffLoadDto.zarfiat = zarfiat;
        onOffLoadDto.hazf = hazf;
        onOffLoadDto.noeVagozariId = noeVagozariId;
        onOffLoadDto.counterStateId = counterStateId;
        onOffLoadDto.possibleAhadMaskooniOrAsli = possibleAhadMaskooniOrAsli;
        onOffLoadDto.possibleAhadTejariOrFari = possibleAhadTejariOrFari;
        onOffLoadDto.possibleAhadSaierOrAbBaha = possibleAhadSaierOrAbBaha;
        onOffLoadDto.possibleEmpty = possibleEmpty;
        onOffLoadDto.possibleKarbariCode = possibleKarbariCode;
        onOffLoadDto.offLoadStateId = offLoadStateId;
        onOffLoadDto.zoneId = zoneId;
        onOffLoadDto.attemptCount = attemptCount;
        onOffLoadDto.highLowStateId = highLowStateId;

        onOffLoadDto.gisAccuracy = gisAccuracy;
        onOffLoadDto.preAverage = preAverage;
        onOffLoadDto.x = x;
        onOffLoadDto.y = y;
        onOffLoadDto.counterNumber = getIntFromString(counterNumber);
        onOffLoadDto.counterStatePosition = getIntFromString(counterStatePosition);

        return onOffLoadDto;
    }


}
