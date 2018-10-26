package com.example.a1024userlogin;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class MfdsSubActivity extends AppCompatActivity {

    WebView webView;
    String link;
    Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mfds_sub);

        webView = (WebView)findViewById(R.id.webView);
        back = (Button)findViewById(R.id.back);

        Intent intent = getIntent();
        link = intent.getStringExtra("link");
        Log.e("tag1",link);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        webView.loadUrl(link);
    }
}
