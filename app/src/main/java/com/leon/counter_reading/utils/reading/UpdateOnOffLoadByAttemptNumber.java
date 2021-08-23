package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.MyApplication.readingData;

import android.os.AsyncTask;

import com.leon.counter_reading.MyApplication;

public class UpdateOnOffLoadByAttemptNumber extends AsyncTask<Void, Void, Void> {
    int position, attemptNumber;

    public UpdateOnOffLoadByAttemptNumber(int position, int attemptNumber) {
        super();
        this.position = position;
        this.attemptNumber = attemptNumber;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        MyApplication.getApplicationComponent().MyDatabase()
                .onOffLoadDao().updateOnOffLoadByAttemptNumber(readingData.onOffLoadDtos.get(position).id, attemptNumber);
        readingData.onOffLoadDtos.get(position).attemptNumber = attemptNumber;
        return null;
    }
}