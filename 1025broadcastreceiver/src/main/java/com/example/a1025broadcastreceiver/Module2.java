package com.example.a1025broadcastreceiver;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Module2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module2);

        Button btn =(Button)findViewById(R.id.btn);
        btn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                //방송을 송신
                Intent intent = new Intent();
                //방송 액션
                intent.setAction("com.example.a1025broadcast");
                //실행한 적이 없어도 설치되어 있으면 방송을 수신
                intent.addFlags(intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                //방송을 송신
                sendBroadcast(intent);
            }
        });
    }
}
