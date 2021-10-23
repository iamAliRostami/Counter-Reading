package com.leon.counter_reading.tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface OnOffLoadDao {
    @Query("select * From OnOffLoadDto WHERE id = :id AND trackNumber = :trackNumber ORDER BY eshterak")
    OnOffLoadDto getAllOnOffLoadById(String id, int trackNumber);

    @Query("select * From OnOffLoadDto Where trackNumber = :trackNumber ORDER BY eshterak")
    List<OnOffLoadDto> getAllOnOffLoadByTracking(int trackNumber);

    @Query("select * From OnOffLoadDto Where trackNumber = :trackNumber AND highLowStateId = :highLow ORDER BY eshterak")
    List<OnOffLoadDto> getAllOnOffLoadByHighLowAndTracking(int trackNumber, int highLow);

    @Query("select * From OnOffLoadDto WHERE offLoadStateId = :offLoadStateId AND trackNumber = :trackNumber ORDER BY eshterak")
    List<OnOffLoadDto> getAllOnOffLoadRead(int offLoadStateId, int trackNumber);

    @Query("select * From OnOffLoadDto WHERE trackNumber = :trackNumber AND " +
            "((counterStateId in (:counterStateId) AND hazf = 0) OR (offLoadStateId = :offLoadStateId)) " +
            "ORDER BY eshterak")
    List<OnOffLoadDto> getOnOffLoadReadByIsManeNotRead(List<Integer> counterStateId, int offLoadStateId, int trackNumber);

    @Query("select * From OnOffLoadDto WHERE offLoadStateId = :offLoadStateId AND trackNumber = :trackNumber ORDER BY eshterak")
    List<OnOffLoadDto> getAllOnOffLoadNotRead(int offLoadStateId, int trackNumber);//TODO

    @Query("select * From OnOffLoadDto WHERE trackNumber = :trackNumber AND offLoadStateId = :offLoadStateId")
    List<OnOffLoadDto> getOnOffLoadReadByTrackingAndOffLoad
            (int trackNumber, int offLoadStateId);

    @Query("select * From OnOffLoadDto WHERE trackNumber = :trackNumber LIMIT 1")
    OnOffLoadDto getOnOffLoadReadByTrackingAndOffLoad(int trackNumber);

    //TODO
    @Query("select OnOffLoadDto.id, OnOffLoadDto.counterNumber, OnOffLoadDto.counterStateId, " +
            "OnOffLoadDto.possibleAddress, OnOffLoadDto.possibleCounterSerial, " +
            "OnOffLoadDto.possibleEshterak, OnOffLoadDto.possibleMobile, " +
            "OnOffLoadDto.possiblePhoneNumber, OnOffLoadDto.possibleAhadMaskooniOrAsli, " +
            "OnOffLoadDto.possibleAhadTejariOrFari, OnOffLoadDto.possibleAhadSaierOrAbBaha, " +
            "OnOffLoadDto.possibleEmpty, OnOffLoadDto.possibleKarbariCode, " +
            "OnOffLoadDto.description, OnOffLoadDto.counterNumberShown, OnOffLoadDto.attemptCount, " +
            "OnOffLoadDto.isLocked, OnOffLoadDto.gisAccuracy, OnOffLoadDto.phoneDateTime, " +
            "OnOffLoadDto.locationDateTime, OnOffLoadDto.x , OnOffLoadDto.y, " +
            "OnOffLoadDto.d1, OnOffLoadDto.d2 From OnOffLoadDto " +
            "Inner JOIN TrackingDto on OnOffLoadDto.trackNumber = TrackingDto.trackNumber " +
            "WHERE OnOffLoadDto.offLoadStateId = :offLoadStateId AND TrackingDto.isActive = :isActive")
    List<OnOffLoadDto.OffLoad> getAllOnOffLoadInsert(int offLoadStateId, boolean isActive);


    @Query("select id, counterNumber, counterStateId, possibleAddress, possibleCounterSerial, " +
            "possibleEshterak, possibleMobile, possiblePhoneNumber, possibleAhadMaskooniOrAsli, " +
            "possibleAhadTejariOrFari, possibleAhadSaierOrAbBaha, possibleEmpty, possibleKarbariCode, " +
            "description, counterNumberShown, attemptCount, isLocked, gisAccuracy, x , y, d1, d2 From OnOffLoadDto " +
            "WHERE offLoadStateId = :offLoadStateId AND trackNumber = :trackNumber")
    List<OnOffLoadDto.OffLoad> getAllOnOffLoadInsert(int offLoadStateId, int trackNumber);

    @Query("select id, counterNumber, counterStateId, possibleAddress, possibleCounterSerial, " +
            "possibleEshterak, possibleMobile, possiblePhoneNumber, possibleAhadMaskooniOrAsli, " +
            "possibleAhadTejariOrFari, possibleAhadSaierOrAbBaha, possibleEmpty, possibleKarbariCode, " +
            "description, counterNumberShown, attemptCount, isLocked, gisAccuracy, x , y, d1, d2 From OnOffLoadDto " +
            "WHERE offLoadStateId = :offLoadStateId AND trackNumber IN (:trackNumber)")
    List<OnOffLoadDto.OffLoad> getAllOnOffLoadInsert(int offLoadStateId, List<Integer> trackNumber);


    @Query("select * From OnOffLoadDto WHERE offLoadStateId = :offLoadStateId")
    List<OnOffLoadDto> getOnOffLoadReadByOffLoad(int offLoadStateId);

    @Query("select * From OnOffLoadDto WHERE counterStateId = :counterStateId AND " +
            "trackNumber = :trackNumber AND hazf = 0 ORDER BY eshterak")
    List<OnOffLoadDto> getOnOffLoadReadByIsMane(int counterStateId, int trackNumber);

    @Query("select * From OnOffLoadDto WHERE counterStateId in (:counterStateId) AND hazf = 0 AND " +
            "trackNumber = :trackNumber  ORDER BY eshterak")
    List<OnOffLoadDto> getOnOffLoadReadByIsMane(List<Integer> counterStateId, int trackNumber);

    @Query("select COUNT(*) From OnOffLoadDto WHERE trackNumber = :trackNumber AND highLowStateId =:highLowStateId")
    int getOnOffLoadReadCountByStatus(int trackNumber, int highLowStateId);

    @Query("select COUNT(*) From OnOffLoadDto WHERE offLoadStateId = :offLoadStateId")
    int getAllOnOffLoadReadCount(int offLoadStateId);

    @Query("select COUNT(*) From OnOffLoadDto WHERE offLoadStateId == :offLoadStateId AND trackNumber = :trackNumber")
    int getOnOffLoadReadCount(int offLoadStateId, int trackNumber);

    @Query("select COUNT(*) From OnOffLoadDto WHERE offLoadStateId = :offLoadStateId AND trackNumber = :trackNumber")
    int getOnOffLoadUnreadCount(int offLoadStateId, int trackNumber);

    @Query("select COUNT(*) From OnOffLoadDto")
    int getAllOnOffLoadCount();

    @Query("select COUNT(*) From OnOffLoadDto WHERE trackNumber = :trackNumber")
    int getOnOffLoadCount(int trackNumber);

    @Query("select COUNT(*) From OnOffLoadDto WHERE counterStateId = :counterStateId AND " +
            "trackNumber = :trackNumber AND hazf = 0")
    int getOnOffLoadIsManeCount(int counterStateId, int trackNumber);

    @Query("select COUNT(*) From OnOffLoadDto WHERE counterStateId = :counterStateId")
    int getAllOnOffLoadIsManeCount(int counterStateId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOnOffLoad(OnOffLoadDto onOffLoadDto);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertAllOnOffLoad(List<OnOffLoadDto> onOffLoadDtos);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateOnOffLoad(OnOffLoadDto onOffLoadDto);

    @Query("UPDATE OnOffLoadDto set offLoadStateId = :offLoadStateId WHERE id = :id")
    void updateOnOffLoad(int offLoadStateId, String id);

    @Query("UPDATE OnOffLoadDto set offLoadStateId = :offLoadStateId WHERE id IN (:id)")
    void updateOnOffLoad(int offLoadStateId, String[] id);

    @Query("Update OnOffLoadDto set offLoadStateId = :offLoadStateId WHERE trackNumber = :trackNumber")
    void updateOnOffLoad(int offLoadStateId, int trackNumber);

    @Query("UPDATE OnOffLoadDto set isBazdid = :isBazdid WHERE id = :id")
    void updateOnOffLoad(boolean isBazdid, String id);

    @Query("UPDATE OnOffLoadDto set possibleCounterSerial = :possibleCounterSerial WHERE id = :id")
    void updateOnOffLoad(String possibleCounterSerial, String id);

    @Query("UPDATE OnOffLoadDto set possibleKarbariCode = :possibleKarbariCode WHERE id = :id")
    void updateOnOffLoad(String id, int possibleKarbariCode);

    @Query("UPDATE OnOffLoadDto set possibleAhadMaskooniOrAsli = :possibleAhadMaskooniOrAsli, " +
            "possibleAhadTejariOrFari = :possibleAhadTejariOrFari WHERE id = :id")
    void updateOnOffLoad(int possibleAhadMaskooniOrAsli, int possibleAhadTejariOrFari, String id);

    @Query("UPDATE OnOffLoadDto set description = :description WHERE id = :id")
    void updateOnOffLoadDescription(String id, String description);

    @Query("UPDATE OnOffLoadDto set d1 = :d1, d2 = :d2 WHERE id = :id")
    void updateOnOffLoadLocation(String id, String d1, String d2);


    @Query("UPDATE OnOffLoadDto set possibleAddress = :address, possibleCounterSerial = :serialNumber," +
            " possibleMobile = :possibleMobile, possibleEshterak = :possibleEshterak," +
            " possiblePhoneNumber = :phoneNumber, possibleEmpty = :possibleEmpty WHERE id = :id")
    void updateOnOffLoad(String id, String possibleEshterak, String possibleMobile, int possibleEmpty,
                         String phoneNumber, String serialNumber, String address);

    @Query("UPDATE OnOffLoadDto set attemptCount = :attemptNumber WHERE id = :id")
    void updateOnOffLoadByAttemptNumber(String id, int attemptNumber);

    @Query("UPDATE OnOffLoadDto set isLocked = :isLocked WHERE trackNumber = :trackNumber")
    void updateOnOffLoadByLock(int trackNumber, boolean isLocked);

    @Query("UPDATE OnOffLoadDto set isLocked = :isLocked WHERE id = :id AND trackNumber = :trackNumber")
    void updateOnOffLoadByLock(String id, int trackNumber, boolean isLocked);

    @Query("DELETE FROM OnOffLoadDto WHERE trackNumber = :trackNumber")
    void deleteOnOffLoad(int trackNumber);

    @Query("DELETE FROM OnOffLoadDto")
    void deleteOnOffLoad();
}
