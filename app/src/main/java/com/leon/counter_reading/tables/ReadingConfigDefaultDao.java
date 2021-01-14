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

    @Query("Select maxAlalHesab From ReadingConfigDefaultDto Where zoneId = :zoneId")
    int getAlalHesabByZoneId(int zoneId);

    @Query("Select * From ReadingConfigDefaultDto Where zoneId = :zoneId")
    List<ReadingConfigDefaultDto> getReadingConfigDefaultDtosByZoneId(int zoneId);

    @Query("Select * From ReadingConfigDefaultDto Where zoneId IN (:zoneId)")
    List<ReadingConfigDefaultDto> getReadingConfigDefaultDtosByZoneId(List<Integer> zoneId);

    @Query("Select * From ReadingConfigDefaultDto Where isArchive = :isArchive")
    List<ReadingConfigDefaultDto> getNotArchiveReadingConfigDefaultDtosByZoneId(boolean isArchive);

    @Query("Select * From ReadingConfigDefaultDto Where zoneId = :zoneId AND isActive = :isActive")
    List<ReadingConfigDefaultDto> getActiveReadingConfigDefaultDtosByZoneId(int zoneId, boolean isActive);

    @Query("Select * From ReadingConfigDefaultDto Where zoneId = :zoneId AND isArchive = :isArchive")
    List<ReadingConfigDefaultDto> getNotArchiveReadingConfigDefaultDtosByZoneId(int zoneId, boolean isArchive);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertReadingConfigDefault(ReadingConfigDefaultDto readingConfigDefault);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllReadingConfigDefault(List<ReadingConfigDefaultDto> readingConfigDefaultDtos);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateReadingConfigDefaultByStatus(ReadingConfigDefaultDto readingConfigDefaultDto);

    @Query("Update ReadingConfigDefaultDto Set isActive = :isActive Where zoneId = :zoneId AND isArchive = 0")
    void updateReadingConfigDefaultByStatus(int zoneId, boolean isActive);

    @Query("Update ReadingConfigDefaultDto Set isArchive = :isArchive, isActive = :isActive Where zoneId = :zoneId")
    void updateReadingConfigDefaultByArchive(int zoneId, boolean isArchive, boolean isActive);

    @Query("Update ReadingConfigDefaultDto Set isArchive = :isArchive, isActive = :isActive")
    void updateReadingConfigDefaultByArchive(boolean isArchive, boolean isActive);
}
