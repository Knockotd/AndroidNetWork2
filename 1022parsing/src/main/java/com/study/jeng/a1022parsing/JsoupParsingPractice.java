package com.study.jeng.a1022parsing;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class JsoupParsingPractice extends AppCompatActivity {
    //ListView 출력을 위한 변수
    ArrayList<String> list;
    ArrayList<String> listlink;
    //Map<String, Object> map;
    //ArrayList<Map<String, Object>> list;
    ArrayAdapter<String> adapter;
    ListView listView;

    //데이터를 다운로드 받는 스레드
    class ThreadEx extends Thread{
     @Override
        public void run(){
         try {
             //다운로드 받은 주소 생성
             String urlAddr = "http://www.bloter.net/";
             URL url = new URL(urlAddr);
             //URL에 연결
             HttpURLConnection conn = (HttpURLConnection) url.openConnection();
             //옵션 설
             conn.setConnectTimeout(20000);
             conn.setUseCaches(false);
             //다운로드 받은 문자열을 저장하기 위한 인스턴스 생성
             StringBuilder sb = new StringBuilder();
             //문자열을다운로드 받기 위한 스트림을 생성
             // 한글이 깨지면 인코딩을 설정해준다. "EUC-KR" 또는 "utf-8"로 설정 ==> 단일 홈페이지일 경우는 이렇게 해도 됨
             //BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
             BufferedReader br = null;
             //헤더 가져오기
             String headerType = conn.getContentType();
             if(headerType.toUpperCase().indexOf("UTF-8") >= 0){
                 //indexOf는 문자열의 시작위치를 리턴하므로 0보다 크면 존재함을 뜻함
                 //이 경우 안드로이드에서는 UTF-8을 생략해도 되지만 자바에서는 써주어야 하므로 그냥 쓰는 걸로 기억
                 br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
             }else{
                 br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "EUC-KR"));
             }
             //문자열을 읽어서 저장
                 while (true) {
                     String line = br.readLine();
                     if (line == null) break;
                     sb.append(line+"\n");
                 }
                 //읽은 데이터 확인
                //Log.e("htnl",sb.toString());
                 //사용한 스트림과 URL 연결 해제
                br.close();
                conn.disconnect();
                //파싱하는 메소드 호출 ( parsing 메소드를 그냥 여기다가 써도 괜찮다 )
                parsing(sb.toString());
         }
         catch (Exception e) {
             Log.e("다운로드 중 에러 발생", e.getMessage());
         }

     }

    }

    public void parsing(String html){
        //impor = jsoup.node
        //HTML을 Memory에 DOM으로 펼치기
        Document dom = Jsoup.parse(html);
        //원하는 항목을 추출
        Elements elements = dom.select("a");
        //iterator를 이용해서 순회
        for(Element element : elements){
            //여는 태그와 닫는 태그 사이의 문자열을 가져오기
            //Log.e("내용",element.text());

            //태그 내의 속성의 값을 가져오기
            //Log.e("속성", element.attr("href"));

            if(element.text().trim().equals(""))continue;
            //태그 안의 내용을 리스트에 출력
            list.add(element.text().trim());
            //태그 내의 href 속성의 값을 리스트에 추가
            listlink.add(element.attr("href"));
        }
        //리스트 뷰를 다시 출력 - 핸들러를 호출해서 수행 (화면을 바꾸는 것은 한꺼번에 동작하므로 중간중간 치고빠질 수 있는 핸들러를 이용)
        handler.sendEmptyMessage(0);
    }

    //리스트 뷰의 내용을 다시 출력할 핸들러
    Handler handler = new Handler(){
      @Override
      public void handleMessage(Message msg){
          adapter.notifyDataSetChanged();
      }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jsoup_parsing_practice);

        list = new ArrayList<String>();
        listlink = new ArrayList<String>();
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, list);
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

        Button btnHtml = (Button)findViewById(R.id.html);

        btnHtml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ThreadEx te = new ThreadEx();
                te.start();
            }
        });

        listView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(listlink.get(position)));
                startActivity(intent);
            }
        });
    }
}
