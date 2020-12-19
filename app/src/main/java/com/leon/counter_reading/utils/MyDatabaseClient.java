package com.leon.counter_reading.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import androidx.room.Room;

import com.leon.counter_reading.MyApplication;

public class MyDatabaseClient {

    private static MyDatabaseClient mInstance;
    private final MyDatabase myDatabase;

    private MyDatabaseClient(Context context) {
        myDatabase = Room.databaseBuilder(context, MyDatabase.class, MyApplication.getDBName())
                .allowMainThreadQueries().build();
    }

    public static synchronized MyDatabaseClient getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyDatabaseClient(context);
        }
        return mInstance;
    }

    public MyDatabase getMyDatabase() {
        return myDatabase;
    }

    public static void migration(Context context) {
        Room.databaseBuilder(context, MyDatabase.class,
                MyApplication.getDBName()).
                fallbackToDestructiveMigration().
                addMigrations(MyDatabase.MIGRATION_6_7).
                allowMainThreadQueries().
                build();
    }

    public static void deleteAndReset(Context context) {
        SQLiteDatabase database;
        database = SQLiteDatabase.openOrCreateDatabase(context.getDatabasePath(MyApplication.getDBName()), null);
        String deleteTable = "DELETE FROM " + MyApplication.getDBName();
        database.execSQL(deleteTable);
        String deleteSqLiteSequence = "DELETE FROM sqlite_sequence WHERE name = '" + MyApplication.getDBName() + "'";
        database.execSQL(deleteSqLiteSequence);
    }
}