package com.leon.counter_reading.tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ReadingConfigDefaultDao {
    @Query("Select * From ReadingConfigDefaultDto WHERE isArchive = :isArchive")
    List<ReadingConfigDefaultDto> getReadingConfigDefaultDtos(boolean isArchive);

    @Query("Select * From ReadingConfigDefaultDto Where isActive = :isActive")
    List<ReadingConfigDefaultDto> getActiveReadingConfigDefaultDtos(boolean isActive);

    @Query("Select * From ReadingConfigDefaultDto Where isActive = :isActive and zoneId = :zoneId AND isArchive = :isArchive")
    List<ReadingConfigDefaultDto> getActiveReadingConfigDefaultDtosByZoneId(int zoneId, boolean isActive, boolean isArchive);

    @Query("Select * From ReadingConfigDefaultDto Where zoneId = :zoneId AND isArchive= :isArchive")
    List<ReadingConfigDefaultDto> getReadingConfigDefaultDtosByZoneId(int zoneId, boolean isArchive);

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
