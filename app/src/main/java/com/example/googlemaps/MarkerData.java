package com.example.googlemaps;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(indices = {@Index(value = {"latitude", "longitude"},unique = true)})
public class MarkerData {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private double latitude;
    private double longitude;

    public MarkerData(double latitude,double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void setId(int id){this.id = id;}
    public int getId(){return id;}

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLatitude() {
        return latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public double getLongitude() {
        return longitude;
    }
}