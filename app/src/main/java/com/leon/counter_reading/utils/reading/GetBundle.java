package com.leon.counter_reading.utils.reading;

import static com.leon.counter_reading.MyApplication.IS_MANE;

import android.os.AsyncTask;
import android.os.Build;

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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                json.forEach(s -> IS_MANE.add(gson.fromJson(s, Integer.class)));
            } else
                for (String s : json) {
                    IS_MANE.add(gson.fromJson(s, Integer.class));
                }
        }
        return null;
    }
}