package com.study.jeng.a1023databaseconnect;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class dataList extends AppCompatActivity {

    SwipeRefreshLayout swipe;

    //ListView 출력관련 변수
    ArrayList<String> list;
    ArrayAdapter adapter;
    ListView listView;

    //상세보기를 위해서 id를 저장할 List
    ArrayList<String> idList;

    //데이터를 다운로드 하는 동안 보여질 대화상자
    ProgressDialog progressDialog;

    //리스트 뷰의 데이터를 다시 출력하는 핸들러
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
            swipe.setRefreshing(false);
        }
    };

    class ThreadEx extends Thread{
        @Override
        public void run(){

            //두 곳에서 모두 쓰고 싶으면 여기서 변수 만들기
            StringBuilder sb = new StringBuilder();

            //다운로드 받는 코드
            try{
                //다운로드 받을 주소 생성
                URL url = new URL("http://192.168.0.215:8080/jeung/idandname");
                //Connection 만들기
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setUseCaches(false);
                con.setConnectTimeout(30000);

                //문자열을 다운로드 받을 스트림 만들기
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                //문자열을 다운로드 받아서 sb에 추가하기
                while(true){
                    String line = br.readLine();
                    if(line == null) break;
                    sb.append(line +"\n");
                }

                br.close();
                con.disconnect();

            }catch (Exception e){
                Log.e("데이터 다운로드 실패 : ",e.getMessage());
            }

            //파싱하는 코드
            try{
                //제체 문자열을 배열로 변경
                JSONArray item1 = new JSONArray(sb.toString());
                //배열 순회
                list.clear();
                idList.clear();
                for(int i=0; i< item1.length(); i=i+1){
                    JSONObject object = item1.getJSONObject(i);
                    //객체에서 itemname 의 값을 가져오사서 nmeListd에 추가
                    list.add(object.getString("itemname"));
                    idList.add(object.getString("itemid"));
                }
                //핸들러 호출 - 다시 출력
                handler.sendEmptyMessage(0);
            }catch (Exception e){
                Log.e("파싱 실패 : ",e.getMessage());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_list);

        list = new ArrayList<>();
        idList = new ArrayList<>();

        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);

        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

        //리스트뷰에서 항목을 클릭했을 때 수행할 내용
        listView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("리스트뷰클릭","도착");
                Intent intent = new Intent(dataList.this, DetailActivity.class);
                //누른번째 뷰의 itemid를 넘겨 줌
                intent.putExtra("itemid", idList.get(position));
                startActivity(intent);
            }
        });

        swipe = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new ThreadEx().start();
            }
        });


    }
    //액티비티가 실행될 때 호출되는 메소드
    @Override
    protected  void onResume(){
        super.onResume();
        progressDialog = ProgressDialog.show(this,"","다운로드 중...");
        new ThreadEx().start();
    }
}
