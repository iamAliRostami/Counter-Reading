package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.helpers.Constants.IS_MANE;
import static com.leon.counter_reading.helpers.Constants.readingData;
import static com.leon.counter_reading.helpers.Constants.readingDataTemp;
import static com.leon.counter_reading.helpers.MyApplication.getApplicationComponent;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.enums.OffloadStateEnum;
import com.leon.counter_reading.enums.ReadStatusEnum;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.tables.ReadingData;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.CustomToast;
import com.leon.counter_reading.utils.MyDatabase;

import java.util.Collections;

public class GetReadingDBData extends AsyncTask<Activity, Integer, Integer> {
    private final CustomProgressModel customProgressModel;
    private final boolean sortType;
    private final int readStatus;
    private final int highLow;

    public GetReadingDBData(Activity activity, int readStatus, int highLow, boolean sortType) {
        super();
        customProgressModel = MyApplication.getApplicationComponent().CustomProgressModel();
        customProgressModel.show(activity, false);
        this.sortType = sortType;
        this.highLow = highLow;
        this.readStatus = readStatus;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        customProgressModel.getDialog().dismiss();
        super.onPostExecute(integer);
    }

    @Override
    protected Integer doInBackground(Activity... activities) {
        readingData = new ReadingData();
        readingDataTemp = new ReadingData();
        MyDatabase myDatabase = getApplicationComponent().MyDatabase();
        readingData.trackingDtos.addAll(myDatabase.trackingDao().
                getTrackingDtosIsActiveNotArchive(true, false));
        for (int i = 0, trackingDtosSize = readingData.trackingDtos.size(); i < trackingDtosSize; i++) {
            TrackingDto trackingDto = readingData.trackingDtos.get(i);
            readingData.readingConfigDefaultDtos.addAll(myDatabase.readingConfigDefaultDao().
                    getReadingConfigDefaultDtosByZoneId(trackingDto.zoneId));
        }

        for (int j = 0, trackingDtosSize = readingData.trackingDtos.size(); j < trackingDtosSize; j++) {
            if (readStatus == ReadStatusEnum.ALL.getValue()) {
                readingData.onOffLoadDtos.addAll(myDatabase.onOffLoadDao().
                        getAllOnOffLoadByTracking(readingData.trackingDtos.get(j).trackNumber));
            } else if (readStatus == ReadStatusEnum.STATE.getValue()) {
                readingData.onOffLoadDtos.addAll(myDatabase.onOffLoadDao().
                        getAllOnOffLoadByHighLowAndTracking(readingData.trackingDtos.get(j).trackNumber, highLow));
            } else if (readStatus == ReadStatusEnum.UNREAD.getValue()) {
                readingData.onOffLoadDtos.addAll(myDatabase.onOffLoadDao().
                        getAllOnOffLoadNotRead(0, readingData.trackingDtos.get(j).trackNumber));
            } else if (readStatus == ReadStatusEnum.READ.getValue()) {
                readingData.onOffLoadDtos.addAll(myDatabase.onOffLoadDao().
                        getAllOnOffLoadRead(OffloadStateEnum.SENT.getValue(), readingData.trackingDtos.get(j).trackNumber));
            } else if (readStatus == ReadStatusEnum.ALL_MANE_UNREAD.getValue()) {
                //TODO
//                for (int k = 0, is_maneSize = IS_MANE.size(); k < is_maneSize; k++) {
//                    int i = IS_MANE.get(k);
//                    readingData.onOffLoadDtos.addAll(myDatabase.onOffLoadDao().
//                            getOnOffLoadReadByIsMane(i, readingData.trackingDtos.get(j).trackNumber));
//                }
//                readingData.onOffLoadDtos.addAll(myDatabase.onOffLoadDao().
//                        getAllOnOffLoadNotRead(0, readingData.trackingDtos.get(j).trackNumber));

                readingData.onOffLoadDtos.addAll(myDatabase.onOffLoadDao().
                        getOnOffLoadReadByIsManeNotRead(IS_MANE, 0,
                                readingData.trackingDtos.get(j).trackNumber));

            } else if (readStatus == ReadStatusEnum.ALL_MANE.getValue()) {
                //TODO
//                for (int k = 0, is_maneSize = IS_MANE.size(); k < is_maneSize; k++) {
//                    int i = IS_MANE.get(k);
//                }
                readingData.onOffLoadDtos.addAll(myDatabase.onOffLoadDao().
                        getOnOffLoadReadByIsMane(IS_MANE, readingData.trackingDtos.get(j).trackNumber));
            }
        }

        if (readingData != null && readingData.onOffLoadDtos != null && readingData.onOffLoadDtos.size() > 0) {
            readingData.counterStateDtos.addAll(myDatabase.counterStateDao().getCounterStateDtos(readingData.onOffLoadDtos.get(0).zoneId));
            if (readingData.counterStateDtos.size() > 0)
                readingDataTemp.counterStateDtos.addAll(readingData.counterStateDtos);
            else {
                activities[0].runOnUiThread(() -> new CustomToast().error(activities[0].getString(R.string.error_on_download_counter_states), Toast.LENGTH_LONG));
                return null;
            }
            readingData.karbariDtos.addAll(myDatabase.karbariDao().getAllKarbariDto());
            if (readingData.karbariDtos.size() > 0)
                readingDataTemp.karbariDtos.addAll(myDatabase.karbariDao().getAllKarbariDto());
            else {
                activities[0].runOnUiThread(() -> new CustomToast().error(activities[0].getString(R.string.error_on_download_karbari), Toast.LENGTH_LONG));
                return null;
            }

            readingData.qotrDictionary.addAll(myDatabase.qotrDictionaryDao().getAllQotrDictionaries());
            if (readingData.qotrDictionary.size() > 0)
                readingDataTemp.qotrDictionary.addAll(myDatabase.qotrDictionaryDao().getAllQotrDictionaries());
            else {
                activities[0].runOnUiThread(() -> new CustomToast().error(activities[0].getString(R.string.error_on_download_qotr), Toast.LENGTH_LONG));
                return null;
            }
            readingDataTemp.onOffLoadDtos.addAll(readingData.onOffLoadDtos);
            readingDataTemp.trackingDtos.addAll(readingData.trackingDtos);
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