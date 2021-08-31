package com.leon.counter_reading.di.view_model;

import android.content.Context;

import androidx.room.Room;

import com.leon.counter_reading.MyApplication;
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
}