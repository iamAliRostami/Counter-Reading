package com.leon.counter_reading.utils.backup_restore;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.leon.counter_reading.BuildConfig;
import com.leon.counter_reading.MyApplication;
import com.leon.counter_reading.di.view_model.CustomProgressModel;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.tables.ReadingConfigDefaultDto;
import com.leon.counter_reading.tables.TrackingDto;
import com.leon.counter_reading.utils.CustomToast;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

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
        restoreReadingConfigDefaultDto(activities[0]);
        restoreOnOffLoadDto(activities[0]);
        restoreTrackingDto(activities[0]);

//        importDatabaseFromCSVFile("CounterStateDto", activities[0]);
//        importDatabaseFromCSVFile("QotrDictionary", activities[0]);
//        importDatabaseFromCSVFile("KarbariDto", activities[0]);
//        importDatabaseFromCSVFile("CounterReportDto", activities[0]);
//        importDatabaseFromCSVFile("OffLoadReport", activities[0]);
//        importDatabaseFromCSVFile("ForbiddenDto", activities[0]);
        return null;
    }

    private void restoreReadingConfigDefaultDto(Activity activity) {
        ArrayList<String> readingConfigDefaultDtoString = importTableFromCSVFile("ReadingConfigDefaultDto", activity);
        ArrayList<ReadingConfigDefaultDto> readingConfigDefaultDtos = new ArrayList<>();
        Gson gson = new Gson();
        for (int i = 0; i < readingConfigDefaultDtoString.size(); i++) {
            ReadingConfigDefaultDtoTemp readingConfigDefaultDtoTemp = gson
                    .fromJson(readingConfigDefaultDtoString.get(i), ReadingConfigDefaultDtoTemp.class);
            readingConfigDefaultDtos.add(readingConfigDefaultDtoTemp.getReadingConfigDto());
        }
        Log.e("size", String.valueOf(readingConfigDefaultDtos.size()));
    }

    private void restoreOnOffLoadDto(Activity activity) {
        ArrayList<String> onOffLoadDtoString = importTableFromCSVFile("OnOffLoadDto", activity);
        ArrayList<OnOffLoadDto> onOffLoadDtos = new ArrayList<>();
        Gson gson = new Gson();
        for (int i = 0; i < onOffLoadDtoString.size(); i++) {
            OnOffLoadDtoTemp onOffLoadDtoTemp = gson
                    .fromJson(onOffLoadDtoString.get(i), OnOffLoadDtoTemp.class);
            onOffLoadDtos.add(onOffLoadDtoTemp.getOnOffLoadDto());
        }
        Log.e("size", String.valueOf(onOffLoadDtos.size()));
    }

    private void restoreTrackingDto(Activity activity) {
        ArrayList<String> trackingDtoString = importTableFromCSVFile("TrackingDto", activity);
        ArrayList<TrackingDto> trackingDtos = new ArrayList<>();
        Gson gson = new Gson();
        for (int i = 0; i < trackingDtoString.size(); i++) {
            TrackingDtoTemp trackingDtoTemp = gson
                    .fromJson(trackingDtoString.get(i), TrackingDtoTemp.class);
            trackingDtos.add(trackingDtoTemp.getTrackingDto());
        }
        Log.e("size", String.valueOf(trackingDtos.size()));
    }

    public static ArrayList<String> importTableFromCSVFile(String tableName, Activity activity) {
        File importDir = new File(String.valueOf(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
        CSVReader csvReader;
        ArrayList<String> value = new ArrayList<>();
        try {
            csvReader = new CSVReader(new FileReader(importDir + "/" + tableName + "_" + BuildConfig.BUILD_TYPE + ".csv"));
            String[] nextLine;
            String[] headerLine = null;
            while ((nextLine = csvReader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                if (headerLine == null) {
                    headerLine = nextLine;
                } else {
                    StringBuilder columns = new StringBuilder();
                    columns.append("{");
                    for (int i = 0; i < nextLine.length - 1; i++) {
                        columns.append("\"").append(headerLine[i]).append("\":\"");
                        if (i == nextLine.length - 2)
                            columns.append(nextLine[i]);
                        else {
                            columns.append(nextLine[i]).append("\",");
                        }
                    }
                    columns.append("\"}");
                    value.add(String.valueOf(columns));
                }
            }
            activity.runOnUiThread(() ->
                    new CustomToast().success("بازیابی اطلاعات با موفقیت انجام شد.", Toast.LENGTH_SHORT));
        } catch (IOException e) {
            e.printStackTrace();
            activity.runOnUiThread(() ->
                    new CustomToast().error("خطا در بازیابی اطلاعات.\n".concat("علت خطا: ")
                            .concat(e.toString()), Toast.LENGTH_LONG));

        }
        return value;
    }

    public static int getIntFromString(String intString) {
        if (intString == null || intString.isEmpty())
            return 0;
        else return Integer.parseInt(intString);
    }

    public static double getDoubleFromString(String doubleString) {
        if (doubleString == null || doubleString.isEmpty())
            return 0;
        else return Double.parseDouble(doubleString);
    }

    public static ArrayList<String> importDatabaseFromCSVFile(String tableName, Activity activity) {
        File importDir = new File(String.valueOf(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
        CSVReader csvReader;
        ArrayList<String> value = new ArrayList<>();
        try {
            csvReader = new CSVReader(new FileReader(importDir + "/" + tableName + "_" + BuildConfig.BUILD_TYPE + ".csv"));
            String[] nextLine;
            String[] headerLine = null;
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
                    value.add(String.valueOf(columns));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            activity.runOnUiThread(() ->
                    new CustomToast().error("خطا در بازیابی اطلاعات.\n".concat("علت خطا: ")
                            .concat(e.toString()), Toast.LENGTH_LONG));

        }
        return value;
    }

    public static void importDatabaseFromCSVFileSample(String tableName, Activity activity) {
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
                        columns.append("\"").append(headerLine[i]).append("\":\"");
                        if (i == nextLine.length - 2)
                            columns.append(nextLine[i]);
                        else {
                            columns.append(nextLine[i]).append("\",");
                        }
                    }
                    columns.append("\"}");
                    Log.e("columns", String.valueOf(columns));
                    Gson gson = new Gson();
                    OnOffLoadDtoTemp temp = gson.fromJson(String.valueOf(columns), OnOffLoadDtoTemp.class);
                    OnOffLoadDto table = temp.getOnOffLoadDto();
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
