package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.MyApplication.getApplicationComponent;
import static com.leon.counter_reading.MyApplication.isMane;
import static com.leon.counter_reading.MyApplication.readingData;
import static com.leon.counter_reading.MyApplication.readingDataTemp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;

import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.enums.OffloadStateEnum;
import com.leon.counter_reading.enums.ReadStatusEnum;
import com.leon.counter_reading.tables.ReadingData;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.CustomProgressBar;
import com.leon.counter_reading.utils.MyDatabase;

import java.util.Collections;

public class GetReadingDBData extends AsyncTask<Activity, Integer, Integer> {
    CustomProgressBar customProgressBar;
    int readStatus, highLow;
    boolean sortType;

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
//        MyDatabase myDatabase = MyDatabaseClient.getInstance(MyApplication.getContext()).getMyDatabase();
        MyDatabase myDatabase = getApplicationComponent().MyDatabase();
        readingData.counterStateDtos.addAll(myDatabase.counterStateDao().getCounterStateDtos());
        readingData.karbariDtos.addAll(myDatabase.karbariDao().getAllKarbariDto());
        readingData.qotrDictionary.addAll(myDatabase.qotrDictionaryDao().getAllQotrDictionaries());
        readingData.trackingDtos.addAll(myDatabase.trackingDao().
                getTrackingDtosIsActiveNotArchive(true, false));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            readingData.trackingDtos.forEach(trackingDto ->
                    readingData.readingConfigDefaultDtos.addAll(myDatabase.readingConfigDefaultDao().
                            getReadingConfigDefaultDtosByZoneId(trackingDto.zoneId)));
        } else {
            for (TrackingDto dto : readingData.trackingDtos) {
                readingData.readingConfigDefaultDtos.addAll(myDatabase.readingConfigDefaultDao().
                        getReadingConfigDefaultDtosByZoneId(dto.zoneId));
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            readingData.trackingDtos.forEach(trackingDto -> {
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
                } else if (readStatus == ReadStatusEnum.ALL_MANE.getValue()) {
                    isMane.forEach(integer ->
                            readingData.onOffLoadDtos.addAll(myDatabase.onOffLoadDao().
                                    getOnOffLoadReadByIsMane(integer, trackingDto.trackNumber)));

                }
            });
        else
            for (TrackingDto trackingDto : readingData.trackingDtos) {
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
                } else if (readStatus == ReadStatusEnum.ALL_MANE.getValue()) {
                    for (int i : isMane) {
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
//            sharedPreferenceManager.getBoolData(SharedReferenceKeys.SORT_TYPE.getValue())
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