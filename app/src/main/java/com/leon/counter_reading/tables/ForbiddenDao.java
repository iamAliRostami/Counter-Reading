package com.leon.counter_reading.tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface ForbiddenDao {
    @Query("SELECT * FROM ForbiddenDto")
    List<ForbiddenDto> getAllForbiddenDto();

    @Query("SELECT * FROM ForbiddenDto WHERE isSent = :isSent")
    List<ForbiddenDto> getAllForbiddenDto(boolean isSent);

    @Insert
    void insertForbiddenDto(ForbiddenDto forbiddenDto);

    @Insert
    void insertForbiddenDto(ArrayList<ForbiddenDto> forbiddenDtos);

    @Query("UPDATE ForbiddenDto set isSent = :isSent WHERE customId = :id")
    void updateForbiddenDtoBySent(boolean isSent, int id);

    @Query("UPDATE ForbiddenDto set isSent = :isSent")
    void updateAllForbiddenDtoBySent(boolean isSent);
}
