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

    @Query("select * From OnOffLoadDto Where trackingId = :trackingId")
    List<OnOffLoadDto> getAllOnOffLoadByTracking(String trackingId);

    @Query("select * From OnOffLoadDto Where trackingId = :trackingId AND highLowStateId = :highLow")
    List<OnOffLoadDto> getAllOnOffLoadByHighLowAndTracking(String trackingId, int highLow);

    @Query("select * From OnOffLoadDto WHERE offLoadStateId = :offLoadStateId AND trackingId = :trackingId")
    List<OnOffLoadDto> getAllOnOffLoadRead(int offLoadStateId, String trackingId);

    @Query("select * From OnOffLoadDto WHERE offLoadStateId = :offLoadStateId")
    List<OnOffLoadDto> getAllOnOffLoadRead(int offLoadStateId);

    @Query("select * From OnOffLoadDto WHERE offLoadStateId != :offLoadStateId AND trackingId = :trackingId")
    List<OnOffLoadDto> getAllOnOffLoadNotRead(int offLoadStateId, String trackingId);

    @Query("select * From OnOffLoadDto WHERE trackingId = :trackingId AND offLoadStateId = :offLoadStateId")
    List<OnOffLoadDto> getOnOffLoadReadByTrackingAndOffLoad
            (String trackingId, int offLoadStateId);

    @Query("select * From OnOffLoadDto WHERE offLoadStateId = :offLoadStateId")
    List<OnOffLoadDto> getOnOffLoadReadByOffLoad(int offLoadStateId);

    @Query("select * From OnOffLoadDto WHERE counterStateId = :counterStateId AND trackingId = :trackingId")
    List<OnOffLoadDto> getOnOffLoadReadByIsMane(int counterStateId, String trackingId);

    @Query("select COUNT(*) From OnOffLoadDto WHERE trackingId = :trackingId AND highLowStateId =:highLowStateId")
    int getOnOffLoadReadCountByStatus(String trackingId, int highLowStateId);

    @Query("select COUNT(*) From OnOffLoadDto WHERE offLoadStateId != :offLoadStateId AND trackingId = :trackingId")
    int getOnOffLoadReadCount(int offLoadStateId, String trackingId);

    @Query("select COUNT(*) From OnOffLoadDto WHERE trackingId = :trackingId")
    int getOnOffLoadCount(String trackingId);

    @Query("select COUNT(*) From OnOffLoadDto WHERE counterStateId = :counterStateId AND trackingId = :trackingId")
    int getOnOffLoadIsManeCount(int counterStateId, String trackingId);

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

    @Query("UPDATE OnOffLoadDto set possibleAhadMaskooniOrAsli = :possibleAhadMaskooniOrAsli, " +
            "possibleAhadTejariOrFari = :possibleAhadTejariOrFari WHERE id = :id")
    void updateOnOffLoad(int possibleAhadMaskooniOrAsli, int possibleAhadTejariOrFari, String id);


    @Query("UPDATE OnOffLoadDto set possibleAddress = :address, possibleCounterSerial = :serialNumber," +
            " possibleMobile = :possibleMobile, possibleEshterak = :possibleEshterak," +
            " possiblePhoneNumber = :phoneNumber WHERE id = :id")
    void updateOnOffLoad(String id, String possibleEshterak, String possibleMobile,
                         String phoneNumber, String serialNumber, String address);

    @Query("DELETE FROM OnOffLoadDto WHERE id = :id")
    void deleteOnOffLoad(String id);

    @Query("DELETE FROM OnOffLoadDto")
    void deleteOnOffLoad();
}
