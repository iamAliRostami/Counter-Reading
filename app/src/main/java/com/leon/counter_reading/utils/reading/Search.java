package com.leon.counter_reading.utils.reading;


import static com.leon.counter_reading.helpers.Constants.readingData;
import static com.leon.counter_reading.helpers.Constants.readingDataTemp;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.enums.SearchTypeEnum;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.utils.CustomToast;

import java.util.ArrayList;

public class Search extends AsyncTask<Activity, Void, Void> {
    private final int type;
    private final String key;
    private final boolean goToPage;

    public Search(int type, String key, boolean goToPage) {
        super();
        this.type = type;
        this.key = key;
        this.goToPage = goToPage;
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        if (type == SearchTypeEnum.NAME.getValue()) {
            readingData.onOffLoadDtos.clear();
            ArrayList<OnOffLoadDto> onOffLoadDtos = readingDataTemp.onOffLoadDtos;
            for (int i = 0, onOffLoadDtosSize = onOffLoadDtos.size(); i < onOffLoadDtosSize; i++) {
                OnOffLoadDto onOffLoadDto = onOffLoadDtos.get(i);
                if (onOffLoadDto.firstName.toLowerCase().contains(key) ||
                        onOffLoadDto.sureName.toLowerCase().contains(key))
                    readingData.onOffLoadDtos.add(onOffLoadDto);
            }
            ((ReadingActivity) (activities[0])).setupViewPager();
        } else {
            boolean found = false;
            int i = 0;
            if (goToPage) {
                if (type == SearchTypeEnum.ESHTERAK.getValue()) {
                    while (i < readingData.onOffLoadDtos.size() && !found) {
                        found = readingData.onOffLoadDtos.get(i).eshterak.contains(key);
                        i++;
                    }
                } else if (type == SearchTypeEnum.RADIF.getValue()) {
                    while (i < readingData.onOffLoadDtos.size() && !found) {
                        found = String.valueOf(readingData.onOffLoadDtos.get(i).radif).contains(key);
                        i++;
                    }
                } else if (type == SearchTypeEnum.BODY_COUNTER.getValue()) {
                    while (i < readingData.onOffLoadDtos.size() && !found) {
                        found = readingData.onOffLoadDtos.get(i).counterSerial.contains(key);
                        i++;
                    }
                }
                if (found)
                    ((ReadingActivity) (activities[0])).changePage(i - 1);
                else
                    activities[0].runOnUiThread(() ->
                            new CustomToast().warning(activities[0].getString(R.string.data_not_found)));
            } else {
                readingData.onOffLoadDtos.clear();
                if (type == SearchTypeEnum.ESHTERAK.getValue()) {
                    ArrayList<OnOffLoadDto> onOffLoadDtos = readingDataTemp.onOffLoadDtos;
                    for (int j = 0, onOffLoadDtosSize = onOffLoadDtos.size(); j < onOffLoadDtosSize; j++) {
                        OnOffLoadDto onOffLoadDto = onOffLoadDtos.get(j);
                        if (onOffLoadDto.eshterak.toLowerCase().contains(key))
                            readingData.onOffLoadDtos.add(onOffLoadDto);
                    }
                } else if (type == SearchTypeEnum.RADIF.getValue()) {
                    ArrayList<OnOffLoadDto> offLoadDtos = readingDataTemp.onOffLoadDtos;//                                if (onOffLoadDto.radif == Integer.parseInt(key))
                    for (int j = 0, offLoadDtosSize = offLoadDtos.size(); j < offLoadDtosSize; j++) {
                        OnOffLoadDto onOffLoadDto = offLoadDtos.get(j);
                        if (String.valueOf(onOffLoadDto.radif).contains(key))
                            readingData.onOffLoadDtos.add(onOffLoadDto);
                    }
                } else if (type == SearchTypeEnum.BODY_COUNTER.getValue()) {
                    ArrayList<OnOffLoadDto> loadDtos = readingDataTemp.onOffLoadDtos;
                    for (int j = 0, loadDtosSize = loadDtos.size(); j < loadDtosSize; j++) {
                        OnOffLoadDto onOffLoadDto = loadDtos.get(j);
                        if (onOffLoadDto.counterSerial.toLowerCase().contains(key))
                            readingData.onOffLoadDtos.add(onOffLoadDto);
                    }
                }
                ((ReadingActivity) (activities[0])).setupViewPager();
            }
        }
        return null;
    }
}
