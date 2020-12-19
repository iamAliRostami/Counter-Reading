package com.leon.counter_reading.tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface OnOffLoadDao {
    @Query("select * From OnOffLoadDto")
    List<OnOffLoadDto> getAllOnOffLoad();

    @Query("select * From OnOffLoadDto Where zoneId = :zoneId")
    List<OnOffLoadDto> getAllOnOffLoadByZone(int zoneId);

    @Query("select * From OnOffLoadDto Where zoneId = :zoneId AND highLowStateId = :highLow")
    List<OnOffLoadDto> getAllOnOffLoadByZone(int zoneId, int highLow);

    @Query("select * From OnOffLoadDto WHERE isBazdid = :isBazdid AND zoneId = :zoneId")
    List<OnOffLoadDto> getAllOnOffLoadRead(boolean isBazdid, int zoneId);

//    @Query("select * From OnOffLoadDto WHERE isBazdid = :isBazdid AND zoneId = :zoneId AND highLowStateId =:highLowStateId")
//    int getOnOffLoadReadCountByStatus(boolean isBazdid, int zoneId, int highLowStateId);

    @Query("select COUNT(*) From OnOffLoadDto WHERE isBazdid = :isBazdid AND zoneId = :zoneId AND highLowStateId =:highLowStateId")
    int getOnOffLoadReadCountByStatus(boolean isBazdid, int zoneId, int highLowStateId);

    @Query("select COUNT(*) From OnOffLoadDto WHERE isBazdid = :isBazdid AND zoneId = :zoneId")
    int getOnOffLoadReadCount(boolean isBazdid, int zoneId);

    @Query("select COUNT(*) From OnOffLoadDto WHERE zoneId = :zoneId")
    int getOnOffLoadCount(int zoneId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOnOffLoad(OnOffLoadDto onOffLoadDto);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAllOnOffLoad(List<OnOffLoadDto> onOffLoadDtos);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateOnOffLoad(OnOffLoadDto onOffLoadDto);
}
