package com.leon.counter_reading.utils;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.leon.counter_reading.tables.CounterStateDao;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.Image;
import com.leon.counter_reading.tables.KarbariDao;
import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.tables.OnOffLoadDao;
import com.leon.counter_reading.tables.OnOffLoadDto;
import com.leon.counter_reading.tables.QotrDictionary;
import com.leon.counter_reading.tables.QotrDictionaryDao;
import com.leon.counter_reading.tables.ReadingConfigDefaultDao;
import com.leon.counter_reading.tables.ReadingConfigDefaultDto;
import com.leon.counter_reading.tables.SavedLocation;
import com.leon.counter_reading.tables.SavedLocationsDao;
import com.leon.counter_reading.tables.TrackingDao;
import com.leon.counter_reading.tables.TrackingDto;

@Database(entities = {SavedLocation.class, KarbariDto.class, OnOffLoadDto.class,
        QotrDictionary.class, ReadingConfigDefaultDto.class, TrackingDto.class,
        CounterStateDto.class, Image.class},
        version = 2, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {
    public abstract KarbariDao karbariDao();

    public abstract OnOffLoadDao onOffLoadDao();

    public abstract QotrDictionaryDao qotrDictionaryDao();

    public abstract ReadingConfigDefaultDao readingConfigDefaultDao();

    public abstract SavedLocationsDao savedLocationDao();

    public abstract CounterStateDao counterStateDao();

    public abstract TrackingDao trackingDao();

    public static final Migration MIGRATION_4_5 = new Migration(16, 17) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE t1_backup AS SELECT * FROM KarbariDto");
            database.execSQL("DROP TABLE KarbariDto");
            database.execSQL("ALTER TABLE t1_backup RENAME TO KarbariDto");
            database.execSQL("DROP TABLE t1_backup");

            database.execSQL("CREATE TABLE t1_backup AS SELECT * FROM SavedLocation");
            database.execSQL("DROP TABLE SavedLocation");
            database.execSQL("ALTER TABLE t1_backup RENAME TO SavedLocation");
            database.execSQL("DROP TABLE t1_backup");

            database.execSQL("CREATE TABLE t1_backup AS SELECT * FROM OnOffLoadDto");
            database.execSQL("DROP TABLE OnOffLoadDto");
            database.execSQL("ALTER TABLE t1_backup RENAME TO OnOffLoadDto");
            database.execSQL("DROP TABLE t1_backup");


            database.execSQL("CREATE TABLE t1_backup AS SELECT * FROM QotrDictionary");
            database.execSQL("DROP TABLE QotrDictionary");
            database.execSQL("ALTER TABLE t1_backup RENAME TO QotrDictionary");
            database.execSQL("DROP TABLE t1_backup");

            database.execSQL("CREATE TABLE t1_backup AS SELECT * FROM ReadingConfigDefaultDto");
            database.execSQL("DROP TABLE ReadingConfigDefaultDto");
            database.execSQL("ALTER TABLE t1_backup RENAME TO ReadingConfigDefaultDto");
            database.execSQL("DROP TABLE t1_backup");

            database.execSQL("CREATE TABLE t1_backup AS SELECT * FROM TrackingDto");
            database.execSQL("DROP TABLE TrackingDto");
            database.execSQL("ALTER TABLE t1_backup RENAME TO TrackingDto");
            database.execSQL("DROP TABLE t1_backup");

            database.execSQL("CREATE TABLE t1_backup AS SELECT * FROM CounterStateDto");
            database.execSQL("DROP TABLE CounterStateDto");
            database.execSQL("ALTER TABLE t1_backup RENAME TO CounterStateDto");
            database.execSQL("DROP TABLE t1_backup");
        }
    };

    public static final Migration MIGRATION_3_4 = new Migration(18, 19) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE t1_backup AS SELECT * FROM TrackingDto");
            database.execSQL("DROP TABLE TrackingDto");
            database.execSQL("ALTER TABLE t1_backup RENAME TO TrackingDto");
            database.execSQL("DROP TABLE t1_backup");
        }
    };

    public static final Migration MIGRATION_6_7 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE \"Image\" (\n" +
                    "\t\"id\"\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                    "\t\"OnOffLoadId\"\tTEXT NOT NULL,\n" +
                    "\t\"Description\"\tTEXT NOT NULL,\n" +
                    "\t\"address\"\tTEXT,\n" +
                    "\t\"isSent\"\tINTEGER,\n" +
                    "\t\"isDeleted\"\tINTEGER,\n" +
                    "\t\"isArchived\"\tINTEGER\n" +
                    ");");
//            database.execSQL("DROP INDEX 'id'");
//            database.execSQL("DROP INDEX 'customId'");
//            database.execSQL("DROP INDEX 'trackNumber'");
//            database.execSQL("DROP INDEX 'moshtarakinId'");
//            database.execSQL("DROP INDEX 'zoneId'");
//            database.execSQL("CREATE UNIQUE INDEX 'customId' ON TrackingDto(customId);");
//            database.execSQL("CREATE UNIQUE INDEX 'customId' ON KarbariDto(customId);");
//            database.execSQL("CREATE UNIQUE INDEX 'customId' ON OnOffLoadDto(customId);");
//            database.execSQL("CREATE UNIQUE INDEX 'customId' ON QotrDictionary(customId);");
//            database.execSQL("CREATE UNIQUE INDEX 'customId' ON ReadingConfigDefaultDto(customId);");
//            database.execSQL("CREATE UNIQUE INDEX 'customId' ON CounterStateDto(customId);");
//
//
//            database.execSQL("CREATE UNIQUE INDEX 'id' ON TrackingDto(id);");
//            database.execSQL("CREATE UNIQUE INDEX 'id' ON KarbariDto(id);");
//            database.execSQL("CREATE UNIQUE INDEX 'id' ON OnOffLoadDto(id);");
//            database.execSQL("CREATE UNIQUE INDEX 'id' ON QotrDictionary(id);");
//            database.execSQL("CREATE UNIQUE INDEX 'id' ON ReadingConfigDefaultDto(id);");
//            database.execSQL("CREATE UNIQUE INDEX 'id' ON CounterStateDto(id);");
//
//
//            database.execSQL("CREATE UNIQUE INDEX 'trackNumber' ON TrackingDto(trackNumber);");
//            database.execSQL("CREATE UNIQUE INDEX 'moshtarakinId' ON KarbariDto(moshtarakinId);");
//            database.execSQL("CREATE UNIQUE INDEX 'id' ON OnOffLoadDto(id);");
//            database.execSQL("CREATE UNIQUE INDEX 'id' ON QotrDictionary(id);");
//            database.execSQL("CREATE UNIQUE INDEX 'zoneId' ON ReadingConfigDefaultDto(zoneId);");
//            database.execSQL("CREATE UNIQUE INDEX 'moshtarakinId' ON CounterStateDto(moshtarakinId);");
        }
    };
    public static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("DROP INDEX 'id'");
//            database.execSQL("DROP INDEX 'id' ON TrackingDto(id);");
//            database.execSQL("DROP INDEX 'id' ON KarbariDto(id);");
//            database.execSQL("DROP INDEX 'id' ON OnOffLoadDto(id);");
//            database.execSQL("DROP INDEX 'id' ON QotrDictionary(id);");
//            database.execSQL("DROP INDEX 'id' ON ReadingConfigDefaultDto(id);");
//            database.execSQL("DROP INDEX 'id' ON CounterStateDto(id);");
        }
    };
}
