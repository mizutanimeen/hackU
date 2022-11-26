package com.example.googlemaps;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.googlemaps.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.sql.Timestamp;
import java.util.List;

public class AddMarker extends FragmentActivity implements OnMapReadyCallback {

    //地図用
    private GoogleMap mMap;

    private static final int RESULT_PICK_IMAGEFILE = 1000;
    private ImageView imageView;
    private String imagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //地図用
        setContentView(R.layout.activity_addmarker);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //初期位置、カメラ、初期設定
        mMap = googleMap;
        LatLng start_location = new LatLng(35.1349, 136.9758);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start_location,15));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        AppDatabase db = AppDatabaseSingleton.getInstance(getApplicationContext());
        Button createBtn = findViewById(R.id.createBtn);
        createBtn.setOnClickListener(new AddMarkerButton(db,this,mMap));

        //プルダウン
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add("安全");
        adapter.add("注意");
        adapter.add("緊急・危険");
        ((Spinner) findViewById(R.id.tag)).setAdapter(adapter);

        Button returnBtn = findViewById(R.id.returnBtn);
        returnBtn.setOnClickListener(new MoveMapsPage());

        //画像追加用
        imageView = (ImageView)findViewById(R.id.image_view);
        findViewById(R.id.imageBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, RESULT_PICK_IMAGEFILE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        super.onActivityResult(requestCode, resultCode, resultData);
        if (requestCode == RESULT_PICK_IMAGEFILE && resultCode == RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                imagePath = uri.toString();

                try {
                    Bitmap bmp = getBitmapFromUri(uri);
                    imageView.setImageBitmap(bmp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private class AddMarkerButton implements View.OnClickListener {
        private AppDatabase db;
        private Activity activity;
        private GoogleMap mMap;

        private AddMarkerButton(AppDatabase db,Activity activity,GoogleMap mMap) {
            this.db = db;
            this.activity = activity;
            this.mMap = mMap;
        }

        @Override
        public void onClick(View view) {
            String title = ((EditText)findViewById(R.id.title)).getText().toString();
            if (title.equals("")){return;}
            String text = ((EditText)findViewById(R.id.text)).getText().toString();
            String tag = (String)((Spinner)findViewById(R.id.tag)).getSelectedItem();

            LatLng point = mMap.getCameraPosition().target;
            double latitude = point.latitude;
            double longitude = point.longitude;
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


            public CreateMarker(AppDatabase db, Activity activity, double latitude,
                                double longitude, String title, String text, String tag) {
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
                markerDao.markerInsert(new MarkerData(latitude, longitude, title, text, tag,
                        new Timestamp(System.currentTimeMillis()).toString(),imagePath));
                return 0;
            }

            @Override
            protected void onPostExecute(Integer code) {
                Activity activity = weakActivity.get();
                if (activity == null) {
                    return;
                }
                Intent intent = new Intent(getApplication(), MapsActivity.class);
                startActivity(intent);
            }
        }
    }

    private class MoveMapsPage implements View.OnClickListener{
        @Override
        public void onClick(View view){
            Intent intent = new Intent(getApplication(), MapsActivity.class);
            startActivity(intent);
        }
    }
}
