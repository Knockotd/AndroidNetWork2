package com.example.a1024userlogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MfdsActivity extends AppCompatActivity {

    //ListView에 출력될 데이터 - M
    ArrayList<String> list;
    ArrayList<HashMap<String, String>> linkList;
    HashMap<String, String> map;
    ArrayList<String> linksave;

    //출력을 위한 ListView - V
    ListView listView;
    //데이터와 ListView를 연결시켜줄 Adapter - C
    //ArrayAdapter는 1개의 데이터만 보여주고 SimpleAdapter는 2개의 데이터를 보여준다.
    SimpleAdapter adapter;

    //진행상황을 출력할 대화상자
    ProgressDialog progressDialog;

    SwipeRefreshLayout swipe;

    //웹에서 다운로드 받을 스레드
    class ThreadEx extends Thread {
        @Override
        public void run() {
            //다운로드 받은 문자열을 저장할 객체
            StringBuilder sb = new StringBuilder();
            try {
                //문자열을 다운로드 받는 코드 영역
                URL url = new URL("https://simpleis-best.tistory.com/rss");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setUseCaches(false);
                con.setConnectTimeout(30000);

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

                while (true) {
                    String line = br.readLine();
                    if (line == null) {
                        break;
                    }
                    sb.append(line + "\n");
                }

                br.close();
                con.disconnect();

                //Log.e("tag1 다운받은 문자열:",sb.toString());

            } catch (Exception e) {
                Log.e("tag0 다운로드 오류:", e.getMessage());
            }
            //XML 파싱
            try{
                //파싱을 수행할 객체 생성
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();

                //다운로드 받은 문자열을 InputStream으로 변환
                InputStream is = new ByteArrayInputStream(sb.toString().getBytes("utf-8"));

                //메모리에 펼치기
                Document doc = builder.parse(is);
                //루트를 가져오기
                Element root = doc.getDocumentElement();

                //여기까지는 그냥 신경 쓰지 말고 가져다 베낄 것.

                //원하는 태그의 데이터를 가져오기
                NodeList items = root.getElementsByTagName("title");
                NodeList linkitems = root.getElementsByTagName("link");
                //Log.e("tag4 items:",items.toString());
                //반복문으로 태그를 순회
                list.clear();
                linkList.clear();
                linksave.clear();
                for(int i=2; i<items.getLength(); i=i+1){
                    map = new HashMap<>();
                    //맵을 여기서 만들어야 해시코드가 새로 배정이 된다.
                    //태그를 하나씩 가져오기
                    Node node = items.item(i);
                    Node node1 = linkitems.item(i);
                    //태그 안의 문자열을 가져와서 리스트에 추가
                    Node contents = node.getFirstChild();
                    Node contents1 = node1.getFirstChild();
                    String title = contents.getNodeValue();
                    String link = contents1.getNodeValue();
                    list.add(title);
                    map.put("title",title);
                    map.put("link",link);
                    linksave.add(map.get("link"));
                    linkList.add(map);
                    //Log.e("tag5 title", linkList.toString());
                }
                //핸들러 호출
                handler.sendEmptyMessage(0);

            }catch (Exception e){
                Log.e("tag3 파싱 오류:",e.getMessage());
            }

        }
    }

    //화면을 갱신할 핸들러
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
            swipe.setRefreshing(false);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mfds);
        //ListView를 출력하기 위한 데이터 생성
        list = new ArrayList<>();
        linkList = new ArrayList<>();
        map = new HashMap<>();
        listView = (ListView) findViewById(R.id.mfdsCardNews);
        adapter = new SimpleAdapter(MfdsActivity.this,linkList,android.R.layout.simple_list_item_2,new String[]{"title","link"},new int []{android.R.id.text1,android.R.id.text2});
        //데이터와 ListView 연결
        listView.setAdapter(adapter);
        linksave = new ArrayList<>();

        swipe = (SwipeRefreshLayout)findViewById(R.id.swipe);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ThreadEx th = new ThreadEx();
                th.start();
            }
        });


        progressDialog = ProgressDialog.show(MfdsActivity.this,"식약청 카드뉴스","다운로드 중...");
        //스레드를 생성하고 시작

        ThreadEx th = new ThreadEx();
        th.start();

        listView.setOnItemClickListener(new ListView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MfdsActivity.this, MfdsSubActivity.class);
                intent.putExtra("link",linksave.get(position));
                startActivity(intent); //특정 액티비티를 고를 수는 없는지? - intent 만들 때 class 정했자노...
            }
        });
    }
}
