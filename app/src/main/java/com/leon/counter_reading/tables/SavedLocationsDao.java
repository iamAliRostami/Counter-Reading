package com.leon.counter_reading.tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SavedLocationsDao {
    @Query("Select * From SavedLocation")
    List<SavedLocation> getAllSavedLocations();

    @Query("Select * From SavedLocation WHERE id BETWEEN :first AND :last")
    List<SavedLocation> getSavedLocations(int first, int last);

    @Query("Select COUNT(*) From SavedLocation")
    int getSavedLocationsCount();

    @Insert
    void insertSavedLocation(SavedLocation savedLocation);

    @Insert
    void insertAllSavedLocations(List<SavedLocation> savedLocations);
}
