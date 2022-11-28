package com.example.googlemaps;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.lang.ref.WeakReference;

public class DetailMarker extends AppCompatActivity {

    private ImageView imageView;
    private int markerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailmarker);

        Intent intent = getIntent();
        markerId = intent.getIntExtra("MARKER_ID", 0);

        Button commentBtn = findViewById(R.id.commentBtn);
        commentBtn.setOnClickListener(new moveCommentPage());

        Button returnBtn = findViewById(R.id.returnBtn);
        returnBtn.setOnClickListener(new MoveMapsPage());

        imageView = findViewById(R.id.imageView);
        new ShowMarkerData(this,markerId).execute();
    }

    private class ShowMarkerData extends AsyncTask<Void, Void, Integer> {
        private WeakReference<Activity> weakActivity;
        private AppDatabase db;
        private int markerId;
        private MarkerData markerData;

        public ShowMarkerData(Activity activity,int markerId) {
            this.db = AppDatabaseSingleton.getInstance(getApplicationContext());
            weakActivity = new WeakReference<>(activity);
            this.markerId = markerId;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            MarkerDataDao markerDao = db.markerDataDao();
            markerData = markerDao.getMarkerDataById(markerId);
            return 0;
        }

        @Override
        protected void onPostExecute(Integer code) {
            Activity activity = weakActivity.get();
            if(activity == null) {
                return;
            }
            Bitmap imageBitmap = StringToBitMap(markerData.getImageBitmap());
            imageView.setImageBitmap(imageBitmap);
        }
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0,
                    encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    private class moveCommentPage implements View.OnClickListener{
        @Override
        public void onClick(View view){
            Intent intent2 = new Intent(getApplication(), CommentPage.class);
            intent2.putExtra("MARKER_ID", markerId);
            startActivity(intent2);
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