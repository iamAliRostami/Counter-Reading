package com.leon.counter_reading.utils.backup_restore;

import com.leon.counter_reading.tables.ForbiddenDto;

public class ForbiddenDtoTemp {
    public String description;
    public String preEshterak;
    public String nextEshterak;
    public String postalCode;
    public String x;
    public String y;
    public String gisAccuracy;
    public String address;

    public int zoneId;
    public int tedadVahed;
    public int isSent;

    public ForbiddenDto getForbiddenDto() {
        ForbiddenDto forbiddenDto = new ForbiddenDto();

        forbiddenDto.description = description;
        forbiddenDto.preEshterak = preEshterak;
        forbiddenDto.nextEshterak = nextEshterak;
        forbiddenDto.postalCode = postalCode;
        forbiddenDto.x = x;
        forbiddenDto.y = y;
        forbiddenDto.gisAccuracy = gisAccuracy;
        forbiddenDto.address = address;

        forbiddenDto.zoneId = zoneId;
        forbiddenDto.tedadVahed = tedadVahed;
        forbiddenDto.isSent = isSent == 1;
        return forbiddenDto;
    }
}