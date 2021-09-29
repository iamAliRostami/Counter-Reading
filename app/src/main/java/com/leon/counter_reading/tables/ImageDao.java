package com.leon.counter_reading.tables;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface ImageDao {
    @Query("SELECT * FROM Image")
    List<Image> getAllImages();

    @Query("SELECT * FROM Image WHERE OnOffLoadId = :OnOffLoadId")
    List<Image> getImagesByOnOffLoadId(String OnOffLoadId);

    @Query("SELECT * FROM Image WHERE id = :id")
    List<Image> getImagesById(int id);

    @Query("SELECT * FROM Image WHERE isSent = :isSent LIMIT 10")
    List<Image> getImagesByBySent(boolean isSent);

    @Query("SELECT COUNT(*) FROM Image WHERE isSent = :isSent AND trackNumber = :trackNumber")
    int getUnsentImageCountByTrackNumber(int trackNumber, boolean isSent);

    @Query("SELECT COUNT(*) FROM Image WHERE isSent = :isSent")
    int getUnsentImageCount(boolean isSent);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertImage(Image image);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllImage(ArrayList<Image> images);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateImage(Image image);

    @Query("DELETE FROM Image WHERE id = :id")
    void deleteImage(int id);
}
