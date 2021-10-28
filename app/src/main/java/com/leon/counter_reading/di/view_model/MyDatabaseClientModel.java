package com.leon.counter_reading.di.view_model;

import android.content.Context;

import androidx.room.Room;

import com.leon.counter_reading.helpers.MyApplication;
import com.leon.counter_reading.utils.MyDatabase;

import javax.inject.Inject;

public class MyDatabaseClientModel {

    private static MyDatabaseClientModel instance;
    private final MyDatabase myDatabase;

    @Inject
    public MyDatabaseClientModel(Context context) {
        myDatabase = Room.databaseBuilder(context, MyDatabase.class, MyApplication.getDBName())
                .allowMainThreadQueries().build();
    }

    public static synchronized MyDatabaseClientModel getInstance(Context context) {
        if (instance == null) {
            instance = new MyDatabaseClientModel(context);
        }
        return instance;
    }

    public static void migration(Context context) {
        Room.databaseBuilder(context, MyDatabase.class,
                MyApplication.getDBName()).
                fallbackToDestructiveMigration().
                addMigrations(MyDatabase.MIGRATION_6_7).
                allowMainThreadQueries().
                build();
    }

    public MyDatabase getMyDatabase() {
        return myDatabase;
    }

//    public static void exportDatabaseToCSVFile(String tableName) {
//        File exportDir = new File(String.valueOf(Environment
//                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
//
////        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
//        if (!exportDir.exists()) {
//            exportDir.mkdirs();
//        }
////        Log.e("directory", exportDir.getAbsolutePath());
//        File file = new File(exportDir, tableName + ".csv");
//        try {
//            file.createNewFile();
//            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
////            Cursor curCSV = db.query("SELECT * FROM " + TableName, null);
//            Cursor curCSV = MyApplication.getApplicationComponent().MyDatabase()
//                    .query("SELECT * FROM " + tableName, null);
//            csvWrite.writeNext(curCSV.getColumnNames());
//            while (curCSV.moveToNext()) {
//                //Which column you want to export
//                String[] arrStr = new String[curCSV.getColumnCount()];
//                for (int i = 0; i < curCSV.getColumnCount() - 1; i++)
//                    arrStr[i] = curCSV.getString(i);
//                csvWrite.writeNext(arrStr);
//            }
//            csvWrite.close();
//            curCSV.close();
//            new CustomToast().success("پشتیبان گیری با موفقیت انجام شد.\n".concat("محل ذخیره سازی: ")
//                    .concat(file.getAbsolutePath()), Toast.LENGTH_LONG);
//        } catch (IOException e) {
//            e.printStackTrace();
//            new CustomToast().error("خطا در پشتیبان گیری.\n".concat("علت خطا: ")
//                    .concat(e.toString()), Toast.LENGTH_LONG);
//        }
//    }
//
//    public static void importDatabaseFromCSVFile(String tableName) {
//        File importDir = new File(String.valueOf(Environment
//                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));
//        CSVReader csvReader;
//        try {
//            csvReader = new CSVReader(new FileReader(importDir + "/" + tableName+".csv"));
//            String[] nextLine;
//            int count = 0;
//            StringBuilder columns = new StringBuilder();
//            StringBuilder value = new StringBuilder();
//            while ((nextLine = csvReader.readNext()) != null) {
//                // nextLine[] is an array of values from the line
//                for (int i = 0; i < nextLine.length - 1; i++) {
//                    if (i == nextLine.length - 2)
//                        columns.append(nextLine[i]);
//                    else
//                        columns.append(nextLine[i]).append(",");
//                    //                    if (count == 0) {
////                        if (i == nextLine.length - 2)
////                            columns.append(nextLine[i]);
////                        else
////                            columns.append(nextLine[i]).append(",");
////                    } else {
////                        if (i == nextLine.length - 2)
////                            value.append("'").append(nextLine[i]).append("'");
////                        else
////                            value.append("'").append(nextLine[i]).append("',");
////                    }
//                }
//                Log.e("row", columns + "-------" + value);
//            }
//            new CustomToast().success("بازیابی اطلاعات با موفقیت انجام شد.", Toast.LENGTH_LONG);
//        } catch (IOException e) {
//            e.printStackTrace();
//            new CustomToast().error("خطا در بازیابی اطلاعات.\n".concat("علت خطا: ")
//                    .concat(e.toString()), Toast.LENGTH_LONG);
//        }
//    }
}