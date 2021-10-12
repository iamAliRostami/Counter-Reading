package com.leon.counter_reading.utils.backup_restore;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.utils.CustomToast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class BackUp extends AsyncTask<Activity, Integer, Void> {
    private final CustomProgressModel customProgressModel;

    public BackUp(Activity activity) {
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
        exportDatabaseToCSVFile("OnOffLoadDto", activities[0]);
        return null;
    }

    public static void exportDatabaseToCSVFile(String tableName, Activity activity) {
        File exportDir = new File(String.valueOf(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File file = new File(exportDir, tableName + ".csv");
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
