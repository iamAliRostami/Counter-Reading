package com.leon.counter_reading.utils.reading;

import android.os.AsyncTask;
import android.os.Build;

import com.google.gson.Gson;

import java.util.ArrayList;

import static com.leon.counter_reading.MyApplication.isMane;

public class GetBundle extends AsyncTask<Void, Void, Void> {
    ArrayList<String> json;

    public GetBundle(ArrayList<String> json) {
        super();
        this.json = json;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (json != null) {
            Gson gson = new Gson();
            isMane.clear();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                json.forEach(s -> isMane.add(gson.fromJson(s, Integer.class)));
            } else
                for (String s : json) {
                    isMane.add(gson.fromJson(s, Integer.class));
                }
        }
        return null;
    }
}