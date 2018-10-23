package com.study.jeng.androidnetwork;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    EditText edit;
    Button input;
    TextView show;

    //진행 상황을 출력할 진행 대화상자
    ProgressDialog pd;

    //데이터를 출력할 핸들러 만들기
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e("핸들러호출/","핸들러 호출됨");
            pd.dismiss();
            show.setText(msg.obj.toString());
        }
    };

    Thread th = new Thread(){}; //=> 클래스 없이 객체만 만드는것 / 1개만 만듦

    //여러번 호출해야 하므로 클래스 만들고 나중에 객체 생성
    class ThreadEx extends Thread{  //=> 클래스를 만들고 객체를 또 따로 만듦 / 여러 개 만들 수 있음
        @Override
        public void run(){
            try{
                Log.e("스레드의 run/","스레드에 도착하면 바로 run을 수행");
                //다운로드 받을 주소 가져오기
                String addr = edit.getText().toString();
                Log.e("editText의 내용/",addr);
                //문자열 주소로 URL 객체 생성
                URL downloadUrl = new URL(addr);
                //연결 객체 생성
                HttpURLConnection con = (HttpURLConnection)downloadUrl.openConnection();
                //옵션 설정
                con.setConnectTimeout(20000);
                con.setUseCaches(false);
                //커넥션이 잘 만들어졌는지 내용을 확인
                Log.e("Connection/",con.toString());
                //문자열 다운로드 받기 위한 스트림 생성
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                while(true){
                    String line = br.readLine();
                    //Log.e("한줄 읽기/",line);
                    if(line == null){
                        break;
                    }
                    sb.append(line + "\n");
                }
                Log.e("읽은 줄 수/",sb.length()+"");
                //전부 가져오면 닫기
                br.close();
                con.disconnect();
                //Message 에 저장해서 handler에게 메시지 전송
                Message msg = new Message();
                msg.obj = sb.toString();
                Log.e("읽은 줄 수/",sb.length()+"");
                handler.sendMessage(msg);
            }catch (Exception e){
                Log.e("스레드 오류",e.getMessage());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit = (EditText)findViewById(R.id.edit);
        input = (Button)findViewById(R.id.input);
        show = (TextView)findViewById(R.id.show);



        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //이 구문이 안 뜨면 버튼의 아이디를 확인
                Log.e("버튼 클릭/","클릭 이벤트를 수행");
                ThreadEx th = new ThreadEx();
                th.start();
                pd = ProgressDialog.show(MainActivity.this,"","loading...");
            }
        });
    }
}
