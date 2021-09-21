package com.leon.counter_reading.tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CounterStateDao {
    @Query("Select * From CounterStateDto")
    List<CounterStateDto> getCounterStateDtos();

    @Query("Select * From CounterStateDto Where zoneId = :zoneId")
    List<CounterStateDto> getCounterStateDtos(int zoneId);

    @Query("Select id From CounterStateDto WHERE isMane = :isMane AND zoneId = :zoneId")
    List<Integer> getCounterStateDtosIsMane(boolean isMane, int zoneId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCounterStateDto(CounterStateDto counterStateDto);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllCounterStateDto(List<CounterStateDto> counterStateDtos);
}
