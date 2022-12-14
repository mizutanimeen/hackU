package com.example.googlemaps;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.sql.Timestamp;
import java.util.List;

@Entity(indices = {@Index(value = {"latitude", "longitude"},unique = true)})
public class MarkerData {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private double latitude;
    private double longitude;

    private String title;
    private String text;
    private String tag;
    private String createTime;
    private String comment;
    private String imageBitmap;

    public int like;

    public MarkerData(double latitude,double longitude,String title,String text,String tag,String createTime,String imageBitmap) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.text = text;
        this.tag = tag;
        this.createTime = createTime;
        this.comment = "";
        this.imageBitmap = imageBitmap;
        this.like = 0;
    }

    public void setId(int id){this.id = id;}
    public int getId(){return id;}

    public void setLatitude(double latitude){this.latitude = latitude;}
    public double getLatitude() {
        return latitude;
    }
    public void setLongitude(double longitude){this.latitude = longitude;}
    public double getLongitude() {return longitude;}

    public void setTitle(String title) {this.title = title;}
    public String getTitle() {return title;}
    public void setText(String text) {this.text = text;}
    public String getText() {return text;}
    public void setCreateTime(String createTime) {this.createTime = createTime;}
    public String getCreateTime() {return createTime;}
    public void setTag(String tag) {this.tag = tag;}
    public String getTag() {return tag;}
    public void setComment(String comment) {this.comment = comment;}
    public String getComment(){return comment;}
    public void setImageBitmap(String imageBitmap){this.imageBitmap = imageBitmap;}
    public String getImageBitmap(){return imageBitmap;}
    public void setLike(int like){this.like = like;}
    public int getLike(){return like;}
}