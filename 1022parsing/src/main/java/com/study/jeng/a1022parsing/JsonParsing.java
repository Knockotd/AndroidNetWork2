package com.study.jeng.a1022parsing;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class JsonParsing extends AppCompatActivity {

    ArrayList<String> list;
    ArrayAdapter<String> adaptor;
    ListView listView;

    class ThreadEx extends Thread{
        //다운로드 받은 문자열을 저장할 변수
        String json = "";
        @Override
        public void run(){
            try{
                //다운로드 받을 주소 생성
                EditText bookName = (EditText)findViewById(R.id.bookName);
                String sam = URLEncoder.encode(bookName.getText().toString(),"utf-8");
                URL url = new URL("https://apis.daum.net/search/book?&output=json&q=" + sam );
                //파라미터는 순서가 상관없기 때문에 q를 마지막 부분은 보내면 ++를 1번으로 줄일 수 있다.


                //URL 연결 객체 생성
                HttpURLConnection con = (HttpURLConnection)url.openConnection();

                //Kakao API 의 인증 설정
                con.setRequestProperty("Authorization", "KakaoAK b16d7284a45e8beff8b1dccb4e8a272f");

                //옵션설정
                //데이터가 계속 변하니까 setUseCaches 이 옵션은 꼭 false로 한다.
                con.setUseCaches(false);
                con.setConnectTimeout(30000);

                //딱 정해진 API를 사용하므로 인코딩 설정을 할 필요가 없습니다.
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                StringBuilder sb = new StringBuilder();

                while(true){
                    String line = br.readLine();
                    if(line == null) break;
                    sb.append(line +"\n");
                }
                json = sb.toString();
                // 다운로드 받은 문자열을 확인
                //Log.e("josn 데이터/",json);

                br.close();
                con.disconnect();
                sb.reverse();

            }catch (Exception e){
                Log.e("다운로드 실패", e.getMessage());
            }
            //json 파싱
            try{
                //문자열을 객체로 생성
                JSONObject book = new JSONObject(json);
                //channel 키의 데이터를 JSONObject 타입으로 가져오
                JSONObject channal = book.getJSONObject("channel");
                //Log.e("channel", channal.toString());
                JSONArray items = channal.getJSONArray("item");
                //Log.e("item", items.toString());
                //배열 데이터를 순회
                list.clear(); //==> 클리어를 안 하면 1번 검색 키워드 결과 뒤에 2번 검색 키워드 결과가 나온다. 즉 누적. 클리어를 해주면 방금 검색한 검색어에 대한 결과만 보인다.
                for(int i=0; i<items.length(); i=i+1){
                    //각 인덱스에 해당하는 항목을 JSONObject로 가져오기
                    JSONObject item = items.getJSONObject(i);
                    //Log.e("item2",item.toString());
                    //list 에 제목가 세틸 가격을 가져와서 추가
                    list.add(
                    item.getString("title") + ":" + item.getString("sale_price"));
                }
                //핸들러를 호출해서 리스트 뷰를 다시 출력하도록 합니다.
                handler.sendEmptyMessage(0);
            }catch (Exception e){
                //에러를 확인할 수 있게 일에 따라서 분리를 시키자.
                Log.e("파싱 에러",e.getMessage());
            }
        }
    }

    Handler handler = new Handler(){
      @Override
      public void handleMessage(Message msg){
          adaptor.notifyDataSetChanged();
      }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json_parsing);

        list = new ArrayList<>();
        adaptor = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adaptor);

        Button jsonBtn = (Button)findViewById(R.id.json);
        jsonBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadEx te = new ThreadEx();
                te.start();
            }
        });

    }
}
