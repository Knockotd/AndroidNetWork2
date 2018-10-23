package com.study.jeng.androidnetwork;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CsvDownload extends AppCompatActivity {

    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    ListView listView;

    Handler handler = new Handler(){
      @Override
      public void handleMessage(Message msg){
          //리스트 뷰 재출력
          adapter.notifyDataSetChanged();
      }
    };

    Thread th = new Thread(){

        @Override
        public void run(){
            try{
                String addr = "http://192.168.0.215:8080/android/data.csv";
                URL url = new URL(addr);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setConnectTimeout(20000);
                con.setUseCaches(false);

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                list.clear();
                while(true){
                    String line = br.readLine();
                    if(line == null){
                        break;
                    }
                    String [] ar = line.split(",");
                    for(String temp : ar){
                        list.add(temp);
                    }
                }
                br.close();
                con.disconnect();
                handler.sendEmptyMessage(0);
            }catch (Exception e){
                Log.e("스레드 오류",e.getMessage());
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csv_download);

        list = new ArrayList<>();
        adapter = new ArrayAdapter<>(CsvDownload.this, android.R.layout.simple_list_item_1, list);
        listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(adapter);

        th.start();
    }
}
