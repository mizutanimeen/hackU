package com.example.googlemaps;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentPage  extends AppCompatActivity {

    private int markerId;

    static ArrayList<Map<String,String>> dataList = new ArrayList<>();
    static SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commentpage);
        dataList = new ArrayList<>();
        Intent intent = getIntent();
        markerId = intent.getIntExtra("MARKER_ID", 0);
        ImageButton returnBtn = findViewById(R.id.returnBtn);
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
            String a = markerData.getComment();
            String a4 = "";
            for(int i=0; i<a.length(); i++) {
                Map<String,String> item = new HashMap<>();
                String a2 = String.valueOf(a.charAt(i));
                String a3 = ",";
                if (!a2.equals(a3)) {
                    a4 = a4.concat(a2);

                } else {
                    item.put("comment",a4);
                    item.put("user","ユーザー１");
                    System.out.println(item);
                    dataList.add(item);
                    System.out.println(dataList);
                    a4 = "";
                }
            }
            if(a4 != ""){
                Map<String,String> item = new HashMap<>();
                item.put("comment",a4);
                item.put("user","ユーザー１");
                dataList.add(item);
            }
            System.out.println(dataList);
            ListView listView = findViewById(R.id.listView1);
            adapter = new SimpleAdapter(
                    CommentPage.this,
                    dataList,
                    android.R.layout.simple_list_item_2,
                    new String[] {"comment", "user"},
            new int[]{android.R.id.text1,android.R.id.text2});
            listView.setAdapter(adapter);
        }
    }

    public void addCommentSetup(View v){
        EditText comment1 = (EditText)findViewById(R.id.comment1);
        String text = comment1.getText().toString();
        comment1.setText("");
        Map<String,String> item = new HashMap<>();
        item.put("comment",text);
        item.put("user","ユーザー１");
        dataList.add(item);
        adapter.notifyDataSetChanged();
        new addComment(CommentPage.this,markerId,text).execute();
    }

    private class addComment extends AsyncTask<Void, Void, Integer> {
        private WeakReference<Activity> weakActivity;
        private AppDatabase db;
        private int markerId;
        private MarkerData markerData;
        private String text;

        public addComment(Activity activity,int markerId,String text) {
            this.db = AppDatabaseSingleton.getInstance(getApplicationContext());
            weakActivity = new WeakReference<>(activity);
            this.markerId = markerId;
            this.text = text;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            MarkerDataDao markerDao = db.markerDataDao();
            markerData = markerDao.getMarkerDataById(markerId);
            if (markerData.getComment().equals("")){
                markerData.setComment(markerData.getComment().concat(text));
            }
            else {
                markerData.setComment(markerData.getComment().concat(",".concat(text)));
            }
            markerDao.updateMarker(markerData);
            return 0;
        }

        @Override
        protected void onPostExecute(Integer code) {
            Activity activity = weakActivity.get();
            if(activity == null) {
                return;
            }
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
