package com.example.googlemaps;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.google.android.gms.maps.model.Marker;

@Dao
public interface MarkerDataDao {
    @Query("SELECT * FROM markerdata")
    List<MarkerData> getAllMarkerData();

    @Query("SELECT id FROM markerdata WHERE latitude = :latitude and longitude = :longitude")
    int getMarkerIdByPosition(double latitude, double longitude);

    @Insert
    void markerInsert(MarkerData markerdata);

    @Query("SELECT * FROM markerdata WHERE id = :id")
    MarkerData getMarkerDataById(int id);
}
