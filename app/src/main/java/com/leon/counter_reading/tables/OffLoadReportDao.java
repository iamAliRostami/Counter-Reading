package com.leon.counter_reading.tables;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface OffLoadReportDao {
    @Query("SELECT * FROM OffLoadReport")
    List<OffLoadReport> getAllOffLoadReport();

    @Query("SELECT * FROM OffLoadReport WHERE onOffLoadId = :id")
    List<OffLoadReport> getAllOffLoadReportById(String id);

    @Query("SELECT * FROM OffLoadReport WHERE onOffLoadId IN (:id)")
    List<OffLoadReport> getAllOffLoadReportById(List<String> id);


    @Query("SELECT * FROM OffLoadReport " +
            "Inner Join OnOffLoadDto On OnOffLoadDto.id = OffLoadreport.onOffLoadId " +
            "Inner Join TrackingDto On OnOffLoadDto.trackNumber = TrackingDto.trackNumber " +
            "WHERE TrackingDto.isActive = :isActive")
    List<OffLoadReport> getAllOffLoadReportByActive(boolean isActive);


    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateOffLoadReport(OffLoadReport offLoadReport);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOffLoadReport(OffLoadReport offLoadReport);

    @Delete
    void deleteOffLoadReport(OffLoadReport offLoadReport);

    @Query("DELETE FROM OffLoadReport WHERE customId = :id")
    void deleteOffLoadReport(int id);

    @Query("DELETE FROM OffLoadReport")
    void deleteAllOffLoadReport();

    @Query("DELETE FROM OffLoadReport WHERE reportId = :reportId AND onOffLoadId = :onOffLoadId")
    void deleteOffLoadReport(int reportId, String onOffLoadId);
}
