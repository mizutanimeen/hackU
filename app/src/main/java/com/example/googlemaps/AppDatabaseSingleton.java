package com.example.googlemaps;

import android.content.Context;
import android.util.Log;

import androidx.room.Room;

public class AppDatabaseSingleton {
    private static AppDatabase instance = null;

    public static AppDatabase getInstance(Context context) {
        if (instance != null) {
            return instance;
        }
        instance = Room.databaseBuilder(context, AppDatabase.class,
                "app_database").fallbackToDestructiveMigration().build();
        return instance;
    }
}