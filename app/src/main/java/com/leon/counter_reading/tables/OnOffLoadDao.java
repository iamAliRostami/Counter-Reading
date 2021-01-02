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

    @Query("select * From OnOffLoadDto WHERE id = :id")
    OnOffLoadDto getAllOnOffLoadById(String id);

    @Query("select * From OnOffLoadDto Where zoneId = :zoneId")
    List<OnOffLoadDto> getAllOnOffLoadByZone(int zoneId);

    @Query("select * From OnOffLoadDto Where zoneId = :zoneId AND highLowStateId = :highLow")
    List<OnOffLoadDto> getAllOnOffLoadByZone(int zoneId, int highLow);

    @Query("select * From OnOffLoadDto WHERE isBazdid = :isBazdid AND zoneId = :zoneId")
    List<OnOffLoadDto> getAllOnOffLoadRead(boolean isBazdid, int zoneId);

    @Query("select * From OnOffLoadDto WHERE " +
            "isBazdid = :isBazdid AND trackingId = :trackingId AND offLoadStateId = :offLoadStateId")
    List<OnOffLoadDto> getOnOffLoadReadByTrackingAndOffLoad
            (boolean isBazdid, String trackingId, int offLoadStateId);

    @Query("select * From OnOffLoadDto WHERE isBazdid = :isBazdid AND offLoadStateId = :offLoadStateId")
    List<OnOffLoadDto> getOnOffLoadReadByTrackingAndOffLoad(boolean isBazdid, int offLoadStateId);

    @Query("select * From OnOffLoadDto WHERE counterStateId = :counterStateId")
    List<OnOffLoadDto> getOnOffLoadReadByIsMane(int counterStateId);

    @Query("select COUNT(*) From OnOffLoadDto WHERE isBazdid = :isBazdid AND zoneId = :zoneId AND highLowStateId =:highLowStateId")
    int getOnOffLoadReadCountByStatus(boolean isBazdid, int zoneId, int highLowStateId);

    @Query("select COUNT(*) From OnOffLoadDto WHERE isBazdid = :isBazdid AND zoneId = :zoneId")
    int getOnOffLoadReadCount(boolean isBazdid, int zoneId);

    @Query("select COUNT(*) From OnOffLoadDto WHERE zoneId = :zoneId")
    int getOnOffLoadCount(int zoneId);

    @Query("select COUNT(*) From OnOffLoadDto WHERE counterStateId = :counterStateId")
    int getOnOffLoadIsManeCount(int counterStateId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOnOffLoad(OnOffLoadDto onOffLoadDto);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAllOnOffLoad(List<OnOffLoadDto> onOffLoadDtos);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateOnOffLoad(OnOffLoadDto onOffLoadDto);

    @Query("UPDATE OnOffLoadDto set offLoadStateId = :offLoadStateId WHERE id = :id")
    void updateOnOffLoad(int offLoadStateId, String id);

    @Query("UPDATE OnOffLoadDto set isBazdid = :isBazdid WHERE id = :id")
    void updateOnOffLoad(boolean isBazdid, String id);

    @Query("UPDATE OnOffLoadDto set possibleCounterSerial = :possibleCounterSerial WHERE id = :id")
    void updateOnOffLoad(String possibleCounterSerial, String id);

    @Query("UPDATE OnOffLoadDto set possibleKarbariCode = :possibleKarbariCode WHERE id = :id")
    void updateOnOffLoad(String id, int possibleKarbariCode);

    @Query("UPDATE OnOffLoadDto set possibleAhadMaskooniOrAsli = :possibleAhadMaskooniOrAsli, possibleAhadTejariOrFari = :possibleAhadTejariOrFari WHERE id = :id")
    void updateOnOffLoad(int possibleAhadMaskooniOrAsli, int possibleAhadTejariOrFari, String id);

    @Query("DELETE FROM OnOffLoadDto WHERE trackingId = :id")
    void deleteOnOffLoad(String id);

    @Query("DELETE FROM OnOffLoadDto")
    void deleteOnOffLoad();
}
