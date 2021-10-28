package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.helpers.Constants.IS_MANE;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.util.ArrayList;

public class GetBundle extends AsyncTask<Void, Void, Void> {
    private final ArrayList<String> json;

    public GetBundle(ArrayList<String> json) {
        super();
        this.json = json;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (json != null) {
            Gson gson = new Gson();
            IS_MANE.clear();
            for (int i = 0, jsonSize = json.size(); i < jsonSize; i++) {
                String s = json.get(i);
                IS_MANE.add(gson.fromJson(s, Integer.class));
            }
        }
        return null;
    }
}