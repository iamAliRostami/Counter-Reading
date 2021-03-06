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

    @Query("Select id From CounterStateDto WHERE isMane = :isMane")
    List<Integer> getCounterStateDtosIsMane(boolean isMane);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCounterStateDto(CounterStateDto counterStateDto);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllCounterStateDto(List<CounterStateDto> counterStateDtos);
}
