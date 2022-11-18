package com.example.googlemaps;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.lang.ref.WeakReference;
import java.sql.Timestamp;
import java.util.List;

public class AddMarker extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmarker);

        Button saveBtn = findViewById(R.id.saveBtn);
        AppDatabase db = AppDatabaseSingleton.getInstance(getApplicationContext());

        saveBtn.setOnClickListener(new AddMarkerButton(db,this));

        //プルダウン
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add("安全");
        adapter.add("注意");
        adapter.add("緊急・危険");
        ((Spinner) findViewById(R.id.tag)).setAdapter(adapter);
    }

    private class AddMarkerButton implements View.OnClickListener {
        private AppDatabase db;
        private Activity activity;

        private AddMarkerButton(AppDatabase db,Activity activity) {
            this.db = db;
            this.activity = activity;
        }

        @Override
        public void onClick(View view) {
            EditText latitudeEdit = findViewById(R.id.latitude);
            EditText longitudeEdit = findViewById(R.id.longitude);
            double latitude = Float.parseFloat(latitudeEdit.getText().toString());
            double longitude = Float.parseFloat(longitudeEdit.getText().toString());
            String title = ((EditText)findViewById(R.id.title)).getText().toString();
            String text = ((EditText)findViewById(R.id.text)).getText().toString();
            String tag = (String)((Spinner)findViewById(R.id.tag)).getSelectedItem();

            new CreateMarker(db,activity, latitude,longitude,title,text,tag).execute();
        }


        private class CreateMarker extends AsyncTask<Void, Void, Integer> {
            private WeakReference<Activity> weakActivity;
            private AppDatabase db;
            private double latitude;
            private double longitude;
            private String title;
            private String text;
            private String tag;


            public CreateMarker(AppDatabase db, Activity activity,double latitude ,
                                double longitude,String title,String text,String tag) {
                this.db = db;
                weakActivity = new WeakReference<>(activity);
                this.latitude = latitude;
                this.longitude = longitude;
                this.title = title;
                this.text = text;
                this.tag = tag;
            }

            @Override
            protected Integer doInBackground(Void... params) {
                MarkerDataDao markerDao = db.markerDataDao();
                markerDao.markerInsert(new MarkerData(latitude,longitude,title,text,tag,
                        new Timestamp(System.currentTimeMillis()).toString()));
                return 0;
            }

            @Override
            protected void onPostExecute(Integer code) {
                Activity activity = weakActivity.get();
                if(activity == null) {
                    return;
                }
                Intent intent = new Intent(getApplication(), MapsActivity.class);
                startActivity(intent);
            }
        }

    }
}
