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
    List<TrackingDto> getTrackingDtos();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTrackingDto(TrackingDto trackingDto);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllTrackingDtos(ArrayList<TrackingDto> trackingDtos);
}
