package com.leon.counter_reading.utils.navigation;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

import com.leon.counter_reading.enums.BundleEnum;
import com.leon.counter_reading.utils.MyDatabaseClient;

public class Navigating extends AsyncTask<Activity, Void, Void> {
    int possibleEmpty, position;
    String uuid, possibleEshterak, possibleMobile, phoneNumber, serialNumber, address;

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
        MyDatabaseClient.getInstance(activities[0]).getMyDatabase().onOffLoadDao().
                updateOnOffLoad(uuid, possibleEshterak, possibleMobile, possibleEmpty, phoneNumber,
                        serialNumber, address);
        Intent intent = new Intent();
        intent.putExtra(BundleEnum.POSITION.getValue(), position);
        intent.putExtra(BundleEnum.BILL_ID.getValue(), uuid);
        activities[0].setResult(RESULT_OK, intent);
        activities[0].finish();
        return null;
    }
}