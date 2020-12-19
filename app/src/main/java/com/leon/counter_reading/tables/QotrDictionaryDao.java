package com.leon.counter_reading.tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface QotrDictionaryDao {
    @Query("Select * From QotrDictionary")
    List<QotrDictionary> getAllQotrDictionaries();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQotrDictionary(QotrDictionary qotrDictionary);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQotrDictionaries(List<QotrDictionary> qotrDictionaries);
}
