package com.leon.counter_reading.utils.backup_restore;

import com.leon.counter_reading.tables.CounterStateDto;

public class CounterStateDtoTemp {
    public String title;

    public int id;
    public int moshtarakinId;
    public int zoneId;
    public int clientOrder;

    public int canEnterNumber;
    public int isMane;
    public int canNumberBeLessThanPre;
    public int isTavizi;
    public int shouldEnterNumber;
    public int isXarab;
    public int isFaqed;

    public CounterStateDto getCounterStateDto() {
        CounterStateDto counterStateDto = new CounterStateDto();
        counterStateDto.title = title;

        counterStateDto.id = id;
        counterStateDto.moshtarakinId = moshtarakinId;
        counterStateDto.zoneId = zoneId;
        counterStateDto.clientOrder = clientOrder;

        counterStateDto.canEnterNumber = canEnterNumber == 1;
        counterStateDto.isMane = isMane == 1;
        counterStateDto.canNumberBeLessThanPre = canNumberBeLessThanPre == 1;
        counterStateDto.isTavizi = isTavizi == 1;
        counterStateDto.shouldEnterNumber = shouldEnterNumber == 1;
        counterStateDto.isXarab = isXarab == 1;
        counterStateDto.isFaqed = isFaqed == 1;
        return counterStateDto;
    }
}