package com.example.googlemaps;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Like extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like);

        ImageButton returnBtn = findViewById(R.id.returnBtn);
        returnBtn.setOnClickListener(new Like.MoveMapsPage());
        new ShowLike(this).execute();
    }

    private class ShowLike extends AsyncTask<Void, Void, Integer> {
        private WeakReference<Activity> weakActivity;
        private AppDatabase db;
        private List<MarkerData> markerList;

        public ShowLike(Activity activity) {
            this.db = AppDatabaseSingleton.getInstance(getApplicationContext());
            weakActivity = new WeakReference<>(activity);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            MarkerDataDao markerDao = db.markerDataDao();
            markerList = markerDao.getAllMarkerDataOrderLike();
            return 0;
        }

        @Override
        protected void onPostExecute(Integer code) {
            Activity activity = weakActivity.get();
            if(activity == null) {
                return;
            }
            ArrayList<Map<String, String>> listData = new ArrayList<>();
            for (MarkerData m : markerList) {
                Map<String, String> item = new HashMap<>();
                item.put("title", m.getTitle());
                item.put("like", "いいね： "+Integer.valueOf(m.getLike()).toString());
                item.put("id",Integer.valueOf(m.getId()).toString());
                listData.add(item);
            }

            // ListViewにデータをセットする
            ListView list = findViewById(R.id.listView1);
            list.setAdapter(new SimpleAdapter(
                    Like.this,
                    listData,
                    android.R.layout.simple_list_item_2,
                    new String[] {"title", "like","id"},
                    new int[] {android.R.id.text1, android.R.id.text2}
            ));

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    System.out.println();
                    int id = Integer.parseInt(listData.get(i).get("id"));
                    Intent intent = new Intent(getApplication(), DetailMarker.class);
                    intent.putExtra("MARKER_ID", id);
                    startActivity(intent);
                }
            });
        }
    }

    private class MoveMapsPage implements View.OnClickListener{
        @Override
        public void onClick(View view){
            finish();
//            Intent intent = new Intent(getApplication(), MapsActivity.class);
//            startActivity(intent);
        }
    }
}
