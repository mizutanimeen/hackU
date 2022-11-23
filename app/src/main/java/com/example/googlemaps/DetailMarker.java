package com.example.googlemaps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetailMarker extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailmarker);

        Intent intent = getIntent();
        int markerId = intent.getIntExtra("MARKER_ID", 0);
        System.out.println(markerId + "を受け取りました。");
    }
}