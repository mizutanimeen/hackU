package com.example.googlemaps;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.util.List;

public class AddMarker extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmarker);

        Button saveBtn = findViewById(R.id.saveBtn);
        AppDatabase db = AppDatabaseSingleton.getInstance(getApplicationContext());

        saveBtn.setOnClickListener(new AddMarkerButton(db,this));
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

            new DataStoreAsyncTask(db,activity, latitude,longitude).execute();
        }


        private class DataStoreAsyncTask extends AsyncTask<Void, Void, Integer> {
            private WeakReference<Activity> weakActivity;
            private AppDatabase db;
            private double latitude;
            private double longitude;

            public DataStoreAsyncTask(AppDatabase db, Activity activity,double latitude ,double longitude) {
                this.db = db;
                weakActivity = new WeakReference<>(activity);
                this.latitude = latitude;
                this.longitude = longitude;
            }

            @Override
            protected Integer doInBackground(Void... params) {
                MarkerDataDao markerDao = db.markerDataDao();
                markerDao.markerInsert(new MarkerData(latitude,longitude));
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
