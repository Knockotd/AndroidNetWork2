package com.example.a1024userlogin;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivityLogin extends AppCompatActivity {

    EditText id, passw;
    LinearLayout linearLayout;
    Button button;
    ProgressDialog progressDialog;

    //스레드를 작업한 후 화면 갱신을 위한 객체
    //1개만 있으면 Message의 what으로 구분해서 사용 가능.
    //따라서 바로 인스턴스 생성
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        progressDialog.dismiss();
        if(msg.what == 1){
            linearLayout.setBackgroundColor(Color.RED);
        }else if(msg.what == 2){
            linearLayout.setBackgroundColor(Color.GREEN);

        }
        }
    };

    //비동기적으로 작업을 수행하기 위한 스레드 클래스
    //스레드는 재사용이 안 되기 때문에 필요할 때마다 인스턴스를 만들어서 사용하므로
    //클래스를 만들어서 사용함.
    class ThreadEx extends Thread {
        @Override
        public void run() {
        try{
            String addr = "http://192.168.0.8:8080/1024androidand/login?id=";
            String logid = id.getText().toString();
            String logpw = passw.getText().toString();
            addr = addr + logid + "&pw=" + logpw;
            Log.e("다운로드 받은 데이터:","??");
            //문자열 주소를 URL로 변경
            URL url = new URL(addr);
            //연결 객체 생성
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            //옵션 설정
            //캐시 사용 여부 - 캐시 : 로컬에 저장해두고 사용
            con.setUseCaches(false);
            //접속을 시도하는 최대 시간 - 30초 동안 접속이 안 되면 예외를 발생 시킴
            con.setConnectTimeout(30000);

            //문자열을 다운로드 받을 스트림 생성(이 때 받으려는 데이터가 문자인지 숫자인지 확인)
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));

            StringBuilder sb = new StringBuilder();
            while(true){
                String line = br.readLine();
                if(line == null) break;
                sb.append(line + "\n");
            }
            br.close();
            con.disconnect();
            Log.e("tag1:",sb.toString());
            //json 파싱
            JSONObject result = new JSONObject(sb.toString());
            String x = result.getString("id");
            //파싱한 결과를 가지고 Message의 what을 달리해서 핸들러에게 전송
            Message msg = new Message();
            if(x.equals("null")){
                //로그인 실패
                Log.e("tag2:","로그인 실패");
                msg.what = 1;
            }else{
                //로그인 성공
                Log.e("tag3:","로그인 성공");
                msg.what = 2;
            }
            handler.sendMessage(msg);
        }catch (Exception e){
            Log.e("다운로드 오류", e.getMessage());
        }
        }
    }

    //Activity가 만들어질 때 호출되는 메소드
    //Activity가 실행될 때 무엇인가를 하고자 하는 경우는 onResume을 사용
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //레이아웃 파일을 읽어서 메모리에 로드한 후 화면출력을 준비하는 메소드를 호출
        setContentView(R.layout.activity_main_login);

        id = (EditText) findViewById(R.id.id);
        passw = (EditText) findViewById(R.id.passw);
        linearLayout = (LinearLayout) findViewById(R.id.layout01);
        button = (Button) findViewById(R.id.loginButton);
        //버튼을 누르면 수행할 내용
        button.setOnClickListener(new Button.OnClickListener(){
            //Listener는 모두 인터페이스라서 implements 에서 찾아야함.
            @Override
            public void onClick(View v) {
                //진행 대화상자를 출력
                progressDialog = ProgressDialog.show(MainActivityLogin.this,"로그인","로그인 중...");
                //스레드를 만들어서 수행
                ThreadEx th = new ThreadEx();
                th.start();
            }
        });
    }
}
