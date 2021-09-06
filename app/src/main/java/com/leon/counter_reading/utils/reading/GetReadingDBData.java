package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.MyApplication.IS_MANE;
import static com.leon.counter_reading.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.MyApplication.readingData;
import static com.leon.counter_reading.MyApplication.readingDataTemp;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.enums.OffloadStateEnum;
import com.leon.counter_reading.enums.ReadStatusEnum;
import com.leon.counter_reading.tables.ReadingData;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.CustomProgressBar;
import com.leon.counter_reading.utils.MyDatabase;

import java.util.Collections;

public class GetReadingDBData extends AsyncTask<Activity, Integer, Integer> {
    private final CustomProgressBar customProgressBar;
    private final boolean sortType;
    private final int readStatus;
    private final int highLow;

    public GetReadingDBData(Activity activity, int readStatus, int highLow, boolean sortType) {
        super();
        customProgressBar = new CustomProgressBar();
        customProgressBar.show(activity, false);
        this.sortType = sortType;
        this.highLow = highLow;
        this.readStatus = readStatus;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Integer integer) {
        customProgressBar.getDialog().dismiss();
        super.onPostExecute(integer);
    }

    @Override
    protected Integer doInBackground(Activity... activities) {
        readingData = new ReadingData();
        readingDataTemp = new ReadingData();
        MyDatabase myDatabase = getApplicationComponent().MyDatabase();
        readingData.counterStateDtos.addAll(myDatabase.counterStateDao().getCounterStateDtos());
        readingData.karbariDtos.addAll(myDatabase.karbariDao().getAllKarbariDto());
        readingData.qotrDictionary.addAll(myDatabase.qotrDictionaryDao().getAllQotrDictionaries());
        readingData.trackingDtos.addAll(myDatabase.trackingDao().
                getTrackingDtosIsActiveNotArchive(true, false));
        for (int i = 0, trackingDtosSize = readingData.trackingDtos.size(); i < trackingDtosSize; i++) {
            TrackingDto trackingDto = readingData.trackingDtos.get(i);
            readingData.readingConfigDefaultDtos.addAll(myDatabase.readingConfigDefaultDao().
                    getReadingConfigDefaultDtosByZoneId(trackingDto.zoneId));
        }

        for (int j = 0, trackingDtosSize = readingData.trackingDtos.size(); j < trackingDtosSize; j++) {
            TrackingDto trackingDto = readingData.trackingDtos.get(j);
            if (readStatus == ReadStatusEnum.ALL.getValue()) {
                readingData.onOffLoadDtos.addAll(myDatabase.onOffLoadDao().
                        getAllOnOffLoadByTracking(trackingDto.trackNumber));
            } else if (readStatus == ReadStatusEnum.STATE.getValue()) {
                readingData.onOffLoadDtos.addAll(myDatabase.onOffLoadDao().
                        getAllOnOffLoadByHighLowAndTracking(trackingDto.trackNumber, highLow));
            } else if (readStatus == ReadStatusEnum.UNREAD.getValue()) {
                readingData.onOffLoadDtos.addAll(myDatabase.onOffLoadDao().
                        getAllOnOffLoadNotRead(0, trackingDto.trackNumber));
            } else if (readStatus == ReadStatusEnum.READ.getValue()) {
                readingData.onOffLoadDtos.addAll(myDatabase.onOffLoadDao().
                        getAllOnOffLoadRead(OffloadStateEnum.SENT.getValue(), trackingDto.trackNumber));
            } else if (readStatus == ReadStatusEnum.ALL_MANE_UNREAD.getValue()) {
                for (int k = 0, is_maneSize = IS_MANE.size(); k < is_maneSize; k++) {
                    int i = IS_MANE.get(k);
                    readingData.onOffLoadDtos.addAll(myDatabase.onOffLoadDao().
                            getOnOffLoadReadByIsMane(i, trackingDto.trackNumber));
                }
                readingData.onOffLoadDtos.addAll(myDatabase.onOffLoadDao().
                        getAllOnOffLoadNotRead(0, trackingDto.trackNumber));

            } else if (readStatus == ReadStatusEnum.ALL_MANE.getValue()) {
                for (int k = 0, is_maneSize = IS_MANE.size(); k < is_maneSize; k++) {
                    int i = IS_MANE.get(k);
                    readingData.onOffLoadDtos.addAll(myDatabase.onOffLoadDao().
                            getOnOffLoadReadByIsMane(i, trackingDto.trackNumber));
                }
            }
        }


        if (readingData != null && readingData.onOffLoadDtos != null && readingData.onOffLoadDtos.size() > 0) {
            readingDataTemp.onOffLoadDtos.addAll(readingData.onOffLoadDtos);
            readingDataTemp.counterStateDtos.addAll(readingData.counterStateDtos);
            readingDataTemp.qotrDictionary.addAll(readingData.qotrDictionary);
            readingDataTemp.trackingDtos.addAll(readingData.trackingDtos);
            readingDataTemp.karbariDtos.addAll(readingData.karbariDtos);
            readingDataTemp.readingConfigDefaultDtos.addAll(readingData.readingConfigDefaultDtos);
            if (sortType) {
                Collections.sort(readingData.onOffLoadDtos, (o1, o2) -> o2.eshterak.compareTo(
                        o1.eshterak));
                Collections.sort(readingDataTemp.onOffLoadDtos, (o1, o2) -> o2.eshterak.compareTo(
                        o1.eshterak));
            }
        }
        ((ReadingActivity) (activities[0])).setupViewPager();
        return null;
    }
}