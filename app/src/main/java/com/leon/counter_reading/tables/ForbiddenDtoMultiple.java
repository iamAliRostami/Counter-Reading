package com.leon.counter_reading.tables;

import okhttp3.MultipartBody;

public class ForbiddenDtoMultiple {
    public final String description;
    public final String preEshterak;
    public final String nextEshterak;
    public final String postalCode;
    public final int tedadVahed;
    public final String x;
    public final String y;
    public final String gisAccuracy;
    public Integer zoneId;

    public MultipartBody.Part File;

    public ForbiddenDtoMultiple(Integer zoneId, String description, String preEshterak,
                                String nextEshterak, String postalCode, int tedadVahed,
                                String x, String y, String gisAccuracy) {
        if (zoneId != 0)
            this.zoneId = zoneId;
        this.description = description;
        this.preEshterak = preEshterak;
        this.nextEshterak = nextEshterak;
        this.postalCode = postalCode;
        this.tedadVahed = tedadVahed;
        this.gisAccuracy = gisAccuracy;
        this.x = x;
        this.y = y;
    }

    public ForbiddenDtoMultiple(ForbiddenDto forbiddenDto) {
        if (forbiddenDto.zoneId != 0)
            this.zoneId = forbiddenDto.zoneId;
        description = forbiddenDto.description;
        preEshterak = forbiddenDto.preEshterak;
        nextEshterak = forbiddenDto.nextEshterak;
        postalCode = forbiddenDto.postalCode;
        tedadVahed = forbiddenDto.tedadVahed;
        gisAccuracy = forbiddenDto.gisAccuracy;
        x = forbiddenDto.x;
        y = forbiddenDto.y;
    }
}
