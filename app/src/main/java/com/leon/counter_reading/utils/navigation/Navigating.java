package com.leon.counter_reading.utils.navigation;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.enums.BundleEnum;

public class Navigating extends AsyncTask<Activity, Void, Void> {
    private final int possibleEmpty;
    private final int position;
    private final String uuid;
    private final String possibleEshterak;
    private final String possibleMobile;
    private final String phoneNumber;
    private final String serialNumber;
    private final String address;

    public Navigating(int position, String uuid, int possibleEmpty, String possibleEshterak,
                      String possibleMobile, String phoneNumber, String serialNumber, String address) {
        super();
        this.possibleEmpty = possibleEmpty;
        this.position = position;
        this.uuid = uuid;
        this.possibleEshterak = possibleEshterak;
        this.possibleMobile = possibleMobile;
        this.phoneNumber = phoneNumber;
        this.serialNumber = serialNumber;
        this.address = address;
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        MyApplication.getApplicationComponent().MyDatabase()
                .onOffLoadDao().updateOnOffLoad(uuid, possibleEshterak, possibleMobile,
                possibleEmpty, phoneNumber, serialNumber, address);
        Intent intent = new Intent();
        intent.putExtra(BundleEnum.POSITION.getValue(), position);
        intent.putExtra(BundleEnum.BILL_ID.getValue(), uuid);
        activities[0].setResult(RESULT_OK, intent);
        activities[0].finish();
        return null;
    }
}