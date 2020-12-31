package com.leon.counter_reading.tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface TrackingDao {
    @Query("Select * From TrackingDto")
    List<TrackingDto> getTrackingDto();

    @Query("Select * From TrackingDto WHERE isArchive = :isArchive")
    List<TrackingDto> getTrackingDtoNotArchive(boolean isArchive);


    @Query("Select * From TrackingDto WHERE isArchive = :isArchive AND isActive = :isActive")
    List<TrackingDto> getTrackingDtosIsActiveNotArchive(boolean isActive, boolean isArchive);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTrackingDto(TrackingDto trackingDto);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllTrackingDtos(ArrayList<TrackingDto> trackingDtos);


    @Query("Update TrackingDto Set isActive = :isActive Where id = :id AND isArchive != 1")
    void updateTrackingDtoByStatus(String id, boolean isActive);

    @Query("Update TrackingDto Set isArchive = :isArchive AND isActive = :isArchive Where id = :id")
    void updateTrackingDtoByArchive(String id, boolean isArchive);

    @Query("Update TrackingDto Set isArchive = :isArchive AND isActive = :isArchive")
    void updateTrackingDtoByArchive(boolean isArchive);
}
