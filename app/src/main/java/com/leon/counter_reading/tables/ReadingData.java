package com.leon.counter_reading.tables;

import java.util.ArrayList;

public class ReadingData {
    public final ArrayList<TrackingDto> trackingDtos;
    public final ArrayList<OnOffLoadDto> onOffLoadDtos;
    public final ArrayList<ReadingConfigDefaultDto> readingConfigDefaultDtos;
    public final ArrayList<KarbariDto> karbariDtos;
    public final ArrayList<QotrDictionary> qotrDictionary;
    public final ArrayList<CounterStateDto> counterStateDtos;
    public final ArrayList<CounterReportDto> counterReportDtos;
    public int status;
    public String message;

    public ReadingData() {
        trackingDtos = new ArrayList<>();
        onOffLoadDtos = new ArrayList<>();
        readingConfigDefaultDtos = new ArrayList<>();
        karbariDtos = new ArrayList<>();
        qotrDictionary = new ArrayList<>();
        counterStateDtos = new ArrayList<>();
        counterReportDtos = new ArrayList<>();
    }
}
