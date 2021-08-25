package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.MyApplication.readingData;
import static com.leon.counter_reading.MyApplication.readingDataTemp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;

import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.utils.CustomToast;

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
        if (type == 3) {
            readingData.onOffLoadDtos.clear();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                readingDataTemp.onOffLoadDtos.forEach(onOffLoadDto -> {
                    if (onOffLoadDto.firstName.toLowerCase().contains(key) ||
                            onOffLoadDto.sureName.toLowerCase().contains(key))
                        readingData.onOffLoadDtos.add(onOffLoadDto);
                });
            } else
                for (OnOffLoadDto onOffLoadDto : readingDataTemp.onOffLoadDtos) {
                    if (onOffLoadDto.firstName.toLowerCase().contains(key) ||
                            onOffLoadDto.sureName.toLowerCase().contains(key))
                        readingData.onOffLoadDtos.add(onOffLoadDto);
                }
            ((ReadingActivity) (activities[0])).setupViewPager();
        } else {
            boolean found = false;
            int i = 0;
            if (goToPage) {
                switch (type) {
                    case 0:
                        while (i < readingData.onOffLoadDtos.size() && !found) {
                            found = readingData.onOffLoadDtos.get(i).eshterak.contains(key);
                            i++;
                        }
                        break;
                    case 1:
                        while (i < readingData.onOffLoadDtos.size() && !found) {
                            found = readingData.onOffLoadDtos.get(i).radif == Integer.parseInt(key);
                            i++;
                        }
                        break;
                    case 2:
                        while (i < readingData.onOffLoadDtos.size() && !found) {
                            found = readingData.onOffLoadDtos.get(i).counterSerial.contains(key);
                            i++;
                        }
                        break;
                }
                if (found)
                    ((ReadingActivity) (activities[0])).changePage(i - 1);
                else
                    activities[0].runOnUiThread(() ->
                            new CustomToast().warning(activities[0].getString(R.string.data_not_found)));
            } else {
                readingData.onOffLoadDtos.clear();
                switch (type) {
                    case 0:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            readingDataTemp.onOffLoadDtos.forEach(onOffLoadDto -> {
                                if (onOffLoadDto.eshterak.toLowerCase().contains(key))
                                    readingData.onOffLoadDtos.add(onOffLoadDto);
                            });
                        } else
                            for (OnOffLoadDto onOffLoadDto : readingDataTemp.onOffLoadDtos) {
                                if (onOffLoadDto.eshterak.toLowerCase().contains(key))
                                    readingData.onOffLoadDtos.add(onOffLoadDto);
                            }
                        break;
                    case 1:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            readingDataTemp.onOffLoadDtos.forEach(onOffLoadDto -> {

                                if (onOffLoadDto.radif == Integer.parseInt(key))
                                    readingData.onOffLoadDtos.add(onOffLoadDto);
                            });
                        } else
                            for (OnOffLoadDto onOffLoadDto : readingDataTemp.onOffLoadDtos) {
                                if (onOffLoadDto.radif == Integer.parseInt(key))
                                    readingData.onOffLoadDtos.add(onOffLoadDto);
                            }
                        break;
                    case 2:
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            readingDataTemp.onOffLoadDtos.forEach(onOffLoadDto -> {
                                if (onOffLoadDto.counterSerial.toLowerCase().contains(key))
                                    readingData.onOffLoadDtos.add(onOffLoadDto);
                            });
                        } else
                            for (OnOffLoadDto onOffLoadDto : readingDataTemp.onOffLoadDtos) {
                                if (onOffLoadDto.counterSerial.toLowerCase().contains(key))
                                    readingData.onOffLoadDtos.add(onOffLoadDto);
                            }
                        break;

                }
                ((ReadingActivity) (activities[0])).setupViewPager();
            }
        }
        return null;
    }
}
