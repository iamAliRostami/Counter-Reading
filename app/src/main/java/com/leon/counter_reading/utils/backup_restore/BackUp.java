package com.leon.counter_reading.utils.backup_restore;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.utils.CustomToast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BackUp extends AsyncTask<Activity, Integer, Void> {
    private final CustomProgressModel customProgressModel;

    public BackUp(Activity activity) {
        super();
        customProgressModel = MyApplication.getApplicationComponent().CustomProgressModel();
        customProgressModel.show(activity, false);
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
        exportDatabaseToCSVFile("ReadingConfigDefaultDto", activities[0]);
        exportDatabaseToCSVFile("TrackingDto", activities[0]);
        exportDatabaseToCSVFile("OnOffLoadDto", activities[0]);
        exportDatabaseToCSVFile("CounterStateDto", activities[0]);
        exportDatabaseToCSVFile("QotrDictionary", activities[0]);
        exportDatabaseToCSVFile("KarbariDto", activities[0]);
        exportDatabaseToCSVFile("CounterReportDto", activities[0]);
        exportDatabaseToCSVFile("OffLoadReport", activities[0]);
        exportDatabaseToCSVFile("ForbiddenDto", activities[0]);
        return null;
    }

    public static void exportDatabaseToCSVFile(String tableName, Activity activity) {
        File exportDir = new File(String.valueOf(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File file = new File(exportDir, tableName + "_" + BuildConfig.BUILD_TYPE + ".csv");
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            Cursor curCSV = MyApplication.getApplicationComponent().MyDatabase()
                    .query("SELECT * FROM " + tableName, null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                //Which column you want to export
                String[] arrStr = new String[curCSV.getColumnCount()];
                for (int i = 0; i < curCSV.getColumnCount() - 1; i++)
                    arrStr[i] = curCSV.getString(i);
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
            activity.runOnUiThread(() ->
                    new CustomToast().success("پشتیبان گیری با موفقیت انجام شد.\n".concat("محل ذخیره سازی: ")
                            .concat(file.getAbsolutePath()), Toast.LENGTH_LONG));
        } catch (IOException e) {
            e.printStackTrace();
            activity.runOnUiThread(() ->
                    new CustomToast().error("خطا در پشتیبان گیری.\n".concat("علت خطا: ")
                            .concat(e.toString()), Toast.LENGTH_LONG));

        }
    }
}
