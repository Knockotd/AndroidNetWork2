package com.example.a1025broadcast;

import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Module1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module1);

        registerReceiver(new MyReceiver(), new IntentFilter("com.example.a1025broadcast"));

    }
}
