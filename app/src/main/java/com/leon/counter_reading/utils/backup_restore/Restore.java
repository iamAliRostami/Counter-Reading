package com.leon.counter_reading.utils.backup_restore;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.utils.CustomToast;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Restore extends AsyncTask<Activity, Integer, Void> {
    private final CustomProgressModel customProgressModel;

    public Restore(Activity activity) {
        super();
        customProgressModel = MyApplication.getApplicationComponent().CustomProgressModel();
        customProgressModel.show(activity, false);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void unused) {
        super.onPostExecute(unused);
        customProgressModel.getDialog().dismiss();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Void doInBackground(Activity... activities) {
        importDatabaseFromCSVFile("ReadingConfigDefaultDto", activities[0]);
        importDatabaseFromCSVFile("TrackingDto", activities[0]);
        importDatabaseFromCSVFile("OnOffLoadDto", activities[0]);
        importDatabaseFromCSVFile("CounterStateDto", activities[0]);
        importDatabaseFromCSVFile("QotrDictionary", activities[0]);
        importDatabaseFromCSVFile("KarbariDto", activities[0]);
        importDatabaseFromCSVFile("CounterReportDto", activities[0]);
        importDatabaseFromCSVFile("OffLoadReport", activities[0]);
        importDatabaseFromCSVFile("ForbiddenDto", activities[0]);
        return null;
    }

    public static void importDatabaseFromCSVFile(String tableName, Activity activity) {
        File importDir = new File(String.valueOf(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
        CSVReader csvReader;
        try {
            csvReader = new CSVReader(new FileReader(importDir + "/" + tableName + "_" + BuildConfig.BUILD_TYPE + ".csv"));
            String[] nextLine;
            String[] headerLine = null;
            StringBuilder value = new StringBuilder();
            value.append("[");
            while ((nextLine = csvReader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                if (headerLine == null) {
                    headerLine = nextLine;
                } else {
                    StringBuilder columns = new StringBuilder();
                    columns.append("{");
                    for (int i = 0; i < nextLine.length - 1; i++) {
                        columns.append("\"").append(headerLine[i]).append("\":");
                        if (i == nextLine.length - 2)
                            columns.append(nextLine[i]);
                        else {
                            columns.append(nextLine[i]).append(",");
                        }
                    }
                    columns.append("}");
                    value.append(columns).append(",");
                }
            }
            try {
                value.replace(value.lastIndexOf(","), value.lastIndexOf(",") + 1, "]");
            } catch (Exception e) {
                value.append("]");
                e.printStackTrace();
            }
            Log.e("value", String.valueOf(value));
            activity.runOnUiThread(() ->
                    new CustomToast().success("بازیابی اطلاعات با موفقیت انجام شد.", Toast.LENGTH_LONG));
        } catch (IOException e) {
            e.printStackTrace();
            activity.runOnUiThread(() ->
                    new CustomToast().error("خطا در بازیابی اطلاعات.\n".concat("علت خطا: ")
                            .concat(e.toString()), Toast.LENGTH_LONG));

        }
    }
}
