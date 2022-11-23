package com.example.googlemaps;

import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.googlemaps.databinding.ActivityMapsBinding;

import java.lang.ref.WeakReference;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //地図用
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //初期位置、カメラ、初期設定
        mMap = googleMap;
        LatLng start_location = new LatLng(0, 0);
//        LatLng start_location = new LatLng(35.1349, 136.9758);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(start_location,15));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        //DBのマーカーを全部設置
        new SetAllMarker(this).execute();

        //ピンクリック時の処理
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return false;
            }
        });

        //情報ウィンドウクリック時
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                LatLng position = marker.getPosition();
                new MoveDetailPageWithId(MapsActivity.this,position.latitude,position.longitude).execute();
            }
        });
    }

    //座標を使ってDBからID取得してページ移動
    private class MoveDetailPageWithId extends AsyncTask<Void, Void, Integer> {
        private WeakReference<Activity> weakActivity;
        private AppDatabase db;
        private double latitude;
        private double longitude;
        private int markerId;

        public MoveDetailPageWithId(Activity activity,double latitude,double longitude) {
            this.db = AppDatabaseSingleton.getInstance(getApplicationContext());
            weakActivity = new WeakReference<>(activity);
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            MarkerDataDao markerDao = db.markerDataDao();
            markerId = markerDao.getMarkerIdByPosition(latitude,longitude);
            return 0;
        }

        @Override
        protected void onPostExecute(Integer code) {
            Activity activity = weakActivity.get();
            if(activity == null) {
                return;
            }
            Intent intent = new Intent(getApplication(), DetailMarker.class);
            intent.putExtra("MARKER_ID", markerId);
            startActivity(intent);
        }
    }

    //DBにあるすべてのマーカーをマップに設置
    private class SetAllMarker extends AsyncTask<Void, Void, Integer> {
        private WeakReference<Activity> weakActivity;
        private AppDatabase db;
        private List<MarkerData> markerList;

        public SetAllMarker(Activity activity) {
            this.db = AppDatabaseSingleton.getInstance(getApplicationContext());
            weakActivity = new WeakReference<>(activity);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            MarkerDataDao markerDao = db.markerDataDao();
            markerList = markerDao.getAllMarkerData();
            return 0;
        }

        @Override
        protected void onPostExecute(Integer code) {
            Activity activity = weakActivity.get();
            if(activity == null) {
                return;
            }
            for (MarkerData m:markerList){
                float color = 0;
                switch (m.getTag()) {
                    // HUE_AZURE HUE_BLUE HUE_CYAN HUE_GREEN HUE_MAZENTA HUE_ORANGE HUE_RED HUE_ROSE HUE_VIOLET HUE_YELLOW
                    case "安全":
                        color = BitmapDescriptorFactory.HUE_BLUE;
                        break;
                    case"注意":
                        color = BitmapDescriptorFactory.HUE_ORANGE;
                        break;
                    case"緊急・危険":
                        color = BitmapDescriptorFactory.HUE_RED;
                        break;
                    default:
                        color = BitmapDescriptorFactory.HUE_BLUE;
                }
                LatLng position = new LatLng(m.getLatitude(), m.getLongitude());
                mMap.addMarker(new MarkerOptions().position(position).title(m.getTitle()).snippet(m.getText())
                        .icon(BitmapDescriptorFactory.defaultMarker(color)));
            }
        }
    }

    public void moveAddPage(View view){
        Intent intent = new Intent(getApplication(), AddMarker.class);
        startActivity(intent);
    }
}