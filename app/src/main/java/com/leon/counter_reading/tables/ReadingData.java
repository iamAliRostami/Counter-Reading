package com.leon.counter_reading.tables;

import java.util.ArrayList;

public class ReadingData {
    public ArrayList<TrackingDto> trackingDtos;
    public ArrayList<OnOffLoadDto> onOffLoadDtos;
    public ArrayList<ReadingConfigDefaultDto> readingConfigDefaultDtos;
    public ArrayList<KarbariDto> karbariDtos;
    public ArrayList<QotrDictionary> qotrDictionary;
    public ArrayList<CounterStateDto> counterStateDtos;

    public ReadingData() {
        trackingDtos = new ArrayList<>();
        onOffLoadDtos = new ArrayList<>();
        readingConfigDefaultDtos = new ArrayList<>();
        karbariDtos = new ArrayList<>();
        qotrDictionary = new ArrayList<>();
        counterStateDtos = new ArrayList<>();
    }
}
