package com.leon.counter_reading.utils;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.leon.counter_reading.tables.CounterReportDao;
import com.leon.counter_reading.tables.CounterReportDto;
import com.leon.counter_reading.tables.CounterStateDao;
import com.leon.counter_reading.tables.CounterStateDto;
import com.leon.counter_reading.tables.ForbiddenDao;
import com.leon.counter_reading.tables.ForbiddenDto;
import com.leon.counter_reading.tables.Image;
import com.leon.counter_reading.tables.ImageDao;
import com.leon.counter_reading.tables.KarbariDao;
import com.leon.counter_reading.tables.KarbariDto;
import com.leon.counter_reading.tables.OffLoadReport;
import com.leon.counter_reading.tables.OffLoadReportDao;
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
import com.leon.counter_reading.tables.Voice;
import com.leon.counter_reading.tables.VoiceDao;

@Database(entities = {SavedLocation.class, KarbariDto.class, OnOffLoadDto.class,
        QotrDictionary.class, ReadingConfigDefaultDto.class, TrackingDto.class, Voice.class,
        CounterStateDto.class, Image.class, CounterReportDto.class, OffLoadReport.class,
        ForbiddenDto.class},
        version = 18, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase {
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
    public static final Migration MIGRATION_6_7 = new Migration(17, 18) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE \"OnOffLoadDtoTemp\" (\n" +
                    "\t\"customId\"\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                    "\t\"id\"\tTEXT,\n" +
                    "\t\"billId\"\tTEXT,\n" +
                    "\t\"radif\"\tINTEGER NOT NULL,\n" +
                    "\t\"eshterak\"\tTEXT,\n" +
                    "\t\"qeraatCode\"\tTEXT,\n" +
                    "\t\"firstName\"\tTEXT,\n" +
                    "\t\"sureName\"\tTEXT,\n" +
                    "\t\"address\"\tTEXT,\n" +
                    "\t\"pelak\"\tTEXT,\n" +
                    "\t\"karbariCode\"\tINTEGER NOT NULL,\n" +
                    "\t\"ahadMaskooniOrAsli\"\tINTEGER NOT NULL,\n" +
                    "\t\"ahadTejariOrFari\"\tINTEGER NOT NULL,\n" +
                    "\t\"ahadSaierOrAbBaha\"\tINTEGER NOT NULL,\n" +
                    "\t\"qotrCode\"\tINTEGER NOT NULL,\n" +
                    "\t\"sifoonQotrCode\"\tINTEGER NOT NULL,\n" +
                    "\t\"postalCode\"\tTEXT,\n" +
                    "\t\"preNumber\"\tINTEGER NOT NULL,\n" +
                    "\t\"preDate\"\tTEXT,\n" +
                    "\t\"preDateMiladi\"\tTEXT,\n" +
                    "\t\"preAverage\"\tREAL NOT NULL,\n" +
                    "\t\"preCounterStateCode\"\tINTEGER NOT NULL,\n" +
                    "\t\"counterSerial\"\tTEXT,\n" +
                    "\t\"counterInstallDate\"\tTEXT,\n" +
                    "\t\"tavizDate\"\tTEXT,\n" +
                    "\t\"tavizNumber\"\tTEXT,\n" +
                    "\t\"trackingId\"\tTEXT,\n" +
                    "\t\"trackNumber\"\tINTEGER,\n" +
                    "\t\"zarfiat\"\tINTEGER NOT NULL,\n" +
                    "\t\"mobile\"\tTEXT,\n" +
                    "\t\"hazf\"\tINTEGER NOT NULL,\n" +
                    "\t\"noeVagozariId\"\tINTEGER NOT NULL,\n" +
                    "\t\"counterNumber\"\tINTEGER NOT NULL,\n" +
                    "\t\"counterStateId\"\tINTEGER NOT NULL,\n" +
                    "\t\"possibleAddress\"\tTEXT,\n" +
                    "\t\"possibleCounterSerial\"\tTEXT,\n" +
                    "\t\"possibleEshterak\"\tTEXT,\n" +
                    "\t\"possibleMobile\"\tTEXT,\n" +
                    "\t\"possiblePhoneNumber\"\tTEXT,\n" +
                    "\t\"possibleAhadMaskooniOrAsli\"\tINTEGER NOT NULL,\n" +
                    "\t\"possibleAhadTejariOrFari\"\tINTEGER NOT NULL,\n" +
                    "\t\"possibleAhadSaierOrAbBaha\"\tINTEGER NOT NULL,\n" +
                    "\t\"possibleEmpty\"\tINTEGER NOT NULL,\n" +
                    "\t\"possibleKarbariCode\"\tINTEGER NOT NULL,\n" +
                    "\t\"description\"\tTEXT,\n" +
                    "\t\"offLoadStateId\"\tINTEGER NOT NULL,\n" +
                    "\t\"zoneId\"\tINTEGER NOT NULL,\n" +
                    "\t\"gisAccuracy\"\tREAL NOT NULL,\n" +
                    "\t\"x\"\tREAL NOT NULL,\n" +
                    "\t\"y\"\tREAL NOT NULL,\n" +
                    "\t\"counterNumberShown\"\tINTEGER NOT NULL,\n" +
                    "\t\"highLowStateId\"\tINTEGER NOT NULL,\n" +
                    "\t\"isBazdid\"\tINTEGER NOT NULL,\n" +
                    "\t\"counterStatePosition\"\tINTEGER\n" +
                    ");");
            database.execSQL("DROP TABLE OnOffLoadDto");
            database.execSQL("ALTER TABLE OnOffLoadDtoTemp RENAME TO OnOffLoadDto");

//            database.execSQL("ALter TABLE OnOffLoadDto Add column trackNumber INTEGER");
//            database.execSQL("ALter TABLE OnOffLoadDto Add column possibleEmpty INTEGER");
//            database.execSQL("CREATE TABLE Voice AS SELECT * FROM Image");
//            database.execSQL("CREATE TABLE \"ForbiddenDto\" (\n" +
//                    "\t\"customId\"\tINTEGER PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
//                    "\t\"zoneId\"\tINTEGER,\n" +
//                    "\t\"description\"\tTEXT,\n" +
//                    "\t\"preEshterak\"\tTEXT,\n" +
//                    "\t\"nextEshterak\"\tTEXT,\n" +
//                    "\t\"postalCode\"\tTEXT,\n" +
//                    "\t\"tedadVahed\"\tINTEGER,\n" +
//                    "\t\"x\"\tTEXT,\n" +
//                    "\t\"y\"\tTEXT,\n" +
//                    "\t\"gisAccuracy\"\tTEXT\n" +
//                    ");");

//            database.execSQL("CREATE TABLE \"OffLoadReport\" (\n" +
//                    "\t\"customId\"\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
//                    "\t\"onOffLoadId\"\tTEXT,\n" +
//                    "\t\"reportId\"\tINTEGER\n" +
//                    ");");
//            database.execSQL("Alter TABLE \"ReadingConfigDefaultDto\" Add column  isArchive Integer;");
//            database.execSQL("Alter TABLE \"OnOffLoadDto\" Add column  counterNumberShown Integer;");
//            database.execSQL("Alter TABLE \"OnOffLoadDto\" Add column  gisAccuracy Real;");
//            database.execSQL("Alter TABLE \"OnOffLoadDto\" Add column  x Real;");
//            database.execSQL("Alter TABLE \"OnOffLoadDto\" Add column  y Real;");


//            database.execSQL("CREATE TABLE \"Image\" (\n" +
//                    "\t\"id\"\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
//                    "\t\"OnOffLoadId\"\tTEXT NOT NULL,\n" +
//                    "\t\"Description\"\tTEXT NOT NULL,\n" +
//                    "\t\"address\"\tTEXT,\n" +
//                    "\t\"isSent\"\tINTEGER,\n" +
//                    "\t\"isDeleted\"\tINTEGER,\n" +
//                    "\t\"isArchived\"\tINTEGER\n" +
//                    ");");
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

    public abstract KarbariDao karbariDao();

    public abstract ImageDao imageDao();

    public abstract OnOffLoadDao onOffLoadDao();

    public abstract QotrDictionaryDao qotrDictionaryDao();

    public abstract ReadingConfigDefaultDao readingConfigDefaultDao();

    public abstract SavedLocationsDao savedLocationDao();

    public abstract CounterStateDao counterStateDao();

    public abstract TrackingDao trackingDao();

    public abstract CounterReportDao counterReportDao();

    public abstract OffLoadReportDao offLoadReportDao();

    public abstract ForbiddenDao forbiddenDao();

    public abstract VoiceDao voiceDao();
}
