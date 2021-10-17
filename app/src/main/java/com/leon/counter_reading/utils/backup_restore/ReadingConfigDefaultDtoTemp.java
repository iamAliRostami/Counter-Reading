package com.leon.counter_reading.utils.backup_restore;

import com.leon.counter_reading.tables.ReadingConfigDefaultDto;

public class ReadingConfigDefaultDtoTemp {
    public String id;
    public int zoneId;
    public int defaultAlalHesab;
    public int maxAlalHesab;
    public int minAlalHesab;
    public int defaultImagePercent;
    public int maxImagePercent;
    public int minImagePercent;
    public int defaultHasPreNumber;
    public int isOnQeraatCode;
    public int displayBillId;
    public int displayRadif;
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
    public int isActive;
    public int isArchive;
    public String zone;

    public ReadingConfigDefaultDto getReadingConfigDto() {
        ReadingConfigDefaultDto readingConfigDefaultDto = new ReadingConfigDefaultDto();
        readingConfigDefaultDto.id = id;
        readingConfigDefaultDto.zoneId = zoneId;
        readingConfigDefaultDto.defaultAlalHesab = defaultAlalHesab;
        readingConfigDefaultDto.maxAlalHesab = maxAlalHesab;
        readingConfigDefaultDto.minAlalHesab = minAlalHesab;
        readingConfigDefaultDto.defaultImagePercent = defaultImagePercent;
        readingConfigDefaultDto.maxImagePercent = maxImagePercent;
        readingConfigDefaultDto.minImagePercent = minImagePercent;
        readingConfigDefaultDto.defaultHasPreNumber = defaultHasPreNumber == 1;
        readingConfigDefaultDto.isOnQeraatCode = isOnQeraatCode == 1;
        readingConfigDefaultDto.displayBillId = displayBillId == 1;
        readingConfigDefaultDto.displayRadif = displayRadif == 1;
        readingConfigDefaultDto.lowConstBoundMaskooni = lowConstBoundMaskooni;
        readingConfigDefaultDto.lowPercentBoundMaskooni = lowPercentBoundMaskooni;
        readingConfigDefaultDto.highConstBoundMaskooni = highConstBoundMaskooni;
        readingConfigDefaultDto.highPercentBoundMaskooni = highPercentBoundMaskooni;
        readingConfigDefaultDto.lowConstBoundSaxt = lowConstBoundSaxt;
        readingConfigDefaultDto.lowPercentBoundSaxt = lowPercentBoundSaxt;
        readingConfigDefaultDto.highConstBoundSaxt = highConstBoundSaxt;
        readingConfigDefaultDto.highPercentBoundSaxt = highPercentBoundSaxt;
        readingConfigDefaultDto.lowConstZarfiatBound = lowConstZarfiatBound;
        readingConfigDefaultDto.lowPercentZarfiatBound = lowPercentZarfiatBound;
        readingConfigDefaultDto.highConstZarfiatBound = highConstZarfiatBound;
        readingConfigDefaultDto.highPercentZarfiatBound = highPercentZarfiatBound;
        readingConfigDefaultDto.lowPercentRateBoundNonMaskooni = lowPercentRateBoundNonMaskooni;
        readingConfigDefaultDto.highPercentRateBoundNonMaskooni = highPercentRateBoundNonMaskooni;
        readingConfigDefaultDto.isActive = isActive == 1;
        readingConfigDefaultDto.isArchive = isArchive == 1;
        readingConfigDefaultDto.zone = zone;
        return readingConfigDefaultDto;
    }
}
