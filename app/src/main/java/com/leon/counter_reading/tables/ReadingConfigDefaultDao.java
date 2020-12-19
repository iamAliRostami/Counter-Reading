package com.leon.counter_reading.tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ReadingConfigDefaultDao {
    @Query("Select * From ReadingConfigDefaultDto")
    List<ReadingConfigDefaultDto> getReadingConfigDefaultDtos();

    @Query("Select * From ReadingConfigDefaultDto Where isActive = :isActive")
    List<ReadingConfigDefaultDto> getActiveReadingConfigDefaultDtos(boolean isActive);

    @Query("Select * From ReadingConfigDefaultDto Where isActive = :isActive and zoneId = :zoneId")
    List<ReadingConfigDefaultDto> getActiveReadingConfigDefaultDtosByZoneId(boolean isActive, int zoneId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertReadingConfigDefault(ReadingConfigDefaultDto readingConfigDefault);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllReadingConfigDefault(List<ReadingConfigDefaultDto> readingConfigDefaultDtos);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateReadingConfigDefaultByStatus(ReadingConfigDefaultDto readingConfigDefaultDto);

    @Query("Update ReadingConfigDefaultDto Set isActive = :isActive Where zoneId = :zoneId")
    void updateReadingConfigDefaultByStatus(boolean isActive, int zoneId);
}
