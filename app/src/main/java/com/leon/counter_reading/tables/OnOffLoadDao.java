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

    @Query("select * From OnOffLoadDto WHERE id = :id ORDER BY eshterak")
    OnOffLoadDto getAllOnOffLoadById(String id);

    @Query("select * From OnOffLoadDto Where trackNumber = :trackNumber ORDER BY eshterak")
    List<OnOffLoadDto> getAllOnOffLoadByTracking(int trackNumber);

    @Query("select * From OnOffLoadDto Where trackNumber = :trackNumber AND highLowStateId = :highLow ORDER BY eshterak")
    List<OnOffLoadDto> getAllOnOffLoadByHighLowAndTracking(int trackNumber, int highLow);

    @Query("select * From OnOffLoadDto WHERE offLoadStateId = :offLoadStateId AND trackNumber = :trackNumber ORDER BY eshterak")
    List<OnOffLoadDto> getAllOnOffLoadRead(int offLoadStateId, int trackNumber);

    @Query("select * From OnOffLoadDto WHERE offLoadStateId = :offLoadStateId ORDER BY eshterak")
    List<OnOffLoadDto> getAllOnOffLoadRead(int offLoadStateId);

    @Query("select * From OnOffLoadDto WHERE offLoadStateId = :offLoadStateId AND trackNumber = :trackNumber ORDER BY eshterak")
    List<OnOffLoadDto> getAllOnOffLoadNotRead(int offLoadStateId, int trackNumber);//TODO

    @Query("select * From OnOffLoadDto WHERE trackNumber = :trackNumber AND offLoadStateId = :offLoadStateId")
    List<OnOffLoadDto> getOnOffLoadReadByTrackingAndOffLoad
            (int trackNumber, int offLoadStateId);

    @Query("select * From OnOffLoadDto WHERE trackNumber = :trackNumber LIMIT 1")
    OnOffLoadDto getOnOffLoadReadByTrackingAndOffLoad
            (int trackNumber);

    @Query("select * From OnOffLoadDto WHERE offLoadStateId = :offLoadStateId")
    List<OnOffLoadDto> getOnOffLoadReadByOffLoad(int offLoadStateId);

    @Query("select * From OnOffLoadDto WHERE counterStateId = :counterStateId AND " +
            "trackNumber = :trackNumber AND hazf = 0 ORDER BY eshterak")
    List<OnOffLoadDto> getOnOffLoadReadByIsMane(int counterStateId, int trackNumber);

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

    @Query("UPDATE OnOffLoadDto set attemptNumber = :attemptNumber WHERE id = :id")
    void updateOnOffLoadByAttemptNumber(String id, int attemptNumber);

    @Query("UPDATE OnOffLoadDto set isLocked = :isLocked WHERE trackNumber = :trackNumber")
    void updateOnOffLoadByLock(int trackNumber, boolean isLocked);

    @Query("DELETE FROM OnOffLoadDto WHERE id = :id")
    void deleteOnOffLoad(String id);

    @Query("DELETE FROM OnOffLoadDto")
    void deleteOnOffLoad();
}
