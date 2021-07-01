package com.leon.counter_reading.tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CounterReportDao {
    @Query("SELECT * FROM CounterReportDto")
    List<CounterReportDto> getAllCounterStateReport();

    @Query("SELECT COUNT(*) FROM CounterReportDto WHERE id = :id")
    int getAllCounterStateReport(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllCounterStateReport(List<CounterReportDto> counterReportDtos);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCounterStateReport(CounterReportDto counterReportDtos);

    @Query("DELETE FROM CounterReportDto")
    void deleteAllCounterReport();
}
