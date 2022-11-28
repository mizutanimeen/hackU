package com.example.googlemaps;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public class CommentPage  extends AppCompatActivity {

    private int markerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commentpage);

        Intent intent = getIntent();
        markerId = intent.getIntExtra("MARKER_ID", 0);
        Button returnBtn = findViewById(R.id.returnBtn);
        returnBtn.setOnClickListener(new moveDetailPage());
        new CommentPage.showComment(this,markerId).execute();
    }

    private class showComment extends AsyncTask<Void, Void, Integer> {
        private WeakReference<Activity> weakActivity;
        private AppDatabase db;
        private int markerId;
        private MarkerData markerData;

        public showComment(Activity activity,int markerId) {
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
            System.out.println("ok" + markerData.getComment());
        }
    }

    private class moveDetailPage implements View.OnClickListener{
        @Override
        public void onClick(View view){
            Intent intent2 = new Intent(getApplication(), DetailMarker.class);
            intent2.putExtra("MARKER_ID", markerId);
            startActivity(intent2);
        }
    }
}
