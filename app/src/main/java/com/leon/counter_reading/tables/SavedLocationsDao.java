package com.leon.counter_reading.tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SavedLocationsDao {
    @Query("Select * From SavedLocation")
    List<SavedLocation> getAllSavedLocations();

    @Insert
    void insertSavedLocation(SavedLocation savedLocation);

    @Insert
    void insertAllSavedLocations(List<SavedLocation> savedLocations);
}
