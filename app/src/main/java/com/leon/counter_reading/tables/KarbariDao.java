package com.leon.counter_reading.tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface KarbariDao {
    @Query("Select * From KarbariDto")
    List<KarbariDto> getAllKarbariDto();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertKarbariDto(KarbariDto karbariDto);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllKarbariDtos(List<KarbariDto> karbariDtos);

    @Query("DELETE FROM KARBARIDTO")
    void deleteKarbariDto();
}
