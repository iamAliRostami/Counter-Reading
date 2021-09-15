package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.MyApplication.readingData;

import android.app.Activity;
import android.os.AsyncTask;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.R;
import com.leon.counter_reading.di.view_model.CustomDialogModel;
import com.leon.counter_reading.enums.DialogType;

public class UpdateOnOffLoadByAttemptNumber extends AsyncTask<Activity, Void, Void> {
    private final int position;
    private final int attemptNumber;

    public UpdateOnOffLoadByAttemptNumber(int position, int attemptNumber) {
        super();
        this.position = position;
        this.attemptNumber = attemptNumber;
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        try {
            MyApplication.getApplicationComponent().MyDatabase()
                    .onOffLoadDao().updateOnOffLoadByAttemptNumber(readingData.onOffLoadDtos.get(position).id, attemptNumber);
            readingData.onOffLoadDtos.get(position).attemptNumber = attemptNumber;
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