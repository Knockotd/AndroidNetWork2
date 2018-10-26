package com.example.a1025broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "아 너무 졸려..", Toast.LENGTH_LONG).show();
        //앱이 실행 중이 아니면 실행하기
        Intent intent1 = new Intent(context, Module1.class);
        //인텐트가 없었으면 새로운 작업을 만들어서 실행하도록 옵셪을 조정
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);

    }
}
