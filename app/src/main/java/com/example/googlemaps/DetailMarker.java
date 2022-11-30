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

    private String noimage = "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";

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
            //タイトル
            TextView title = findViewById(R.id.title);
            title.setText(markerData.getTitle());

            //画像
            Bitmap imageBitmap = StringToBitMap(markerData.getImageBitmap());
            if (markerData.getImageBitmap() == null){
                imageBitmap = StringToBitMap(noimage);
            }
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