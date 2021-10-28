package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.helpers.Constants.readingData;
import static com.leon.counter_reading.helpers.Constants.readingDataTemp;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.activities.ReadingActivity;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.enums.DialogType;

public class UpdateOnOffLoadDtoByLock extends AsyncTask<Activity, Void, Void> {
    private final int position;
    private final int trackNumber;
    private final String id;

    public UpdateOnOffLoadDtoByLock(int position, int trackNumber, String id) {
        super();
        this.position = position;
        this.trackNumber = trackNumber;
        this.id = id;
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        try {
            int i = 0;
            boolean found = false;
            while (!found && i < readingDataTemp.onOffLoadDtos.size()) {
                if (readingDataTemp.onOffLoadDtos.get(i).id.equals(id)) {
                    readingDataTemp.onOffLoadDtos.get(i).isLocked = true;
                    found = true;
                }
                i++;
            }
            MyApplication.getApplicationComponent().MyDatabase().onOffLoadDao().updateOnOffLoadByLock(id, trackNumber, true);
            readingData.onOffLoadDtos.get(position).isLocked = true;
            ((ReadingActivity) (activities[0])).setupViewPagerAdapter(position);
        } catch (Exception e) {
            activities[0].runOnUiThread(() -> new CustomDialogModel(DialogType.Red,
                    activities[0], e.getMessage(),
                    activities[0].getString(R.string.dear_user),
                    activities[0].getString(R.string.take_screen_shot),
                    activities[0].getString(R.string.accepted)));
        }
        return null;
    }
}