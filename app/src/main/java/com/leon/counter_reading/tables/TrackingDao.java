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

    @Query("Select alalHesabPercent From TrackingDto Where zoneId = :zoneId")
    int getAlalHesabByZoneId(int zoneId);

//    @Query("SELECT * FROM TrackingDto INNER JOIN ReadingConfigDefaultDto ON " +
//            "TrackingDto.zoneId=ReadingConfigDefaultDto.zoneId " +
//            "WHERE ReadingConfigDefaultDto.id = :id")
//    List<TrackingDto> getUsersForRepository(String id);

    @Query("Select zoneId From TrackingDto WHERE isArchive = :isArchive AND isActive = :isActive")
    List<Integer> getZoneIdIsActiveNotArchive(boolean isActive, boolean isArchive);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTrackingDto(TrackingDto trackingDto);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllTrackingDtos(ArrayList<TrackingDto> trackingDtos);

    @Query("Update TrackingDto Set isActive = :isActive Where id = :id AND isArchive = 0")
    void updateTrackingDtoByStatus(String id, boolean isActive);

    @Query("Update TrackingDto Set isLocked = :isLocked Where trackNumber = :trackNumber")
    void updateTrackingDtoByLock(int trackNumber, boolean isLocked);

    @Query("Update TrackingDto Set isArchive = :isArchive, isActive = :isActive Where id = :id")
    void updateTrackingDtoByArchive(String id, boolean isArchive, boolean isActive);

    @Query("Update TrackingDto Set isArchive = :isArchive, isActive = :isActive")
    void updateTrackingDtoByArchive(boolean isArchive, boolean isActive);

    @Query("DELETE FROM TrackingDto WHERE trackNumber = :trackNumber AND isArchive = :isArchive")
    void deleteTrackingDto(int trackNumber, boolean isArchive);

    @Query("SELECT COUNT(*) FROM TrackingDto WHERE isActive = :isActive AND isArchive = :isArchive")
    int getTrackingDtoActivesCount(boolean isActive, boolean isArchive);

    @Query("SELECT COUNT(*) FROM TrackingDto WHERE trackNumber= :trackNumber")
    int getTrackingDtoActivesCountByTracking(int trackNumber);

    @Query("SELECT COUNT(*) FROM TrackingDto WHERE trackNumber= :trackNumber AND isArchive = :isArchive")
    int getTrackingDtoArchiveCountByTrackNumber(int trackNumber, boolean isArchive);
}
