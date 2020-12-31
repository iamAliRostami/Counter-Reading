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

    @Query("Select * From ReadingConfigDefaultDto Where zoneId = :zoneId")
    List<ReadingConfigDefaultDto> getReadingConfigDefaultDtosByZoneId(int zoneId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertReadingConfigDefault(ReadingConfigDefaultDto readingConfigDefault);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllReadingConfigDefault(List<ReadingConfigDefaultDto> readingConfigDefaultDtos);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateReadingConfigDefaultByStatus(ReadingConfigDefaultDto readingConfigDefaultDto);

    @Query("Update ReadingConfigDefaultDto Set isActive = :isActive Where zoneId = :zoneId")
    void updateReadingConfigDefaultByStatus(int zoneId, boolean isActive);

    @Query("Update ReadingConfigDefaultDto Set isArchive = :isArchive Where zoneId = :zoneId")
    void updateReadingConfigDefaultByArchive(int zoneId, boolean isArchive);

    @Query("Update ReadingConfigDefaultDto Set isArchive = :isArchive")
    void updateReadingConfigDefaultByArchive(boolean isArchive);
}
