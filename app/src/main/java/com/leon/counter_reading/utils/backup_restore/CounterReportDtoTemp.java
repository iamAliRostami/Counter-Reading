package com.leon.counter_reading.utils.backup_restore;

import com.leon.counter_reading.tables.CounterReportDto;

public class CounterReportDtoTemp {
    public int id;
    public int moshtarakinId;
    public String title;
    public int zoneId;
    public int isAhad;
    public int isKarbari;
    public int canNumberBeLessThanPre;
    public int isTavizi;
    public int clientOrder;

    public CounterReportDto getCounterReportDto() {
        CounterReportDto counterReportDto = new CounterReportDto();

        counterReportDto.id = id;
        counterReportDto.moshtarakinId = moshtarakinId;
        counterReportDto.title = title;
        counterReportDto.zoneId = zoneId;
        counterReportDto.isAhad = isAhad == 1;
        counterReportDto.isKarbari = isKarbari == 1;
        counterReportDto.canNumberBeLessThanPre = canNumberBeLessThanPre == 1;
        counterReportDto.isTavizi = isTavizi == 1;
        counterReportDto.clientOrder = clientOrder;
        return counterReportDto;
    }
}
