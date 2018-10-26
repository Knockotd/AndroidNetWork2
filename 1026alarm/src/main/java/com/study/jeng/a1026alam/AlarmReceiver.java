package com.study.jeng.a1026alam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.MediaController;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //알람이 왔을 때 실행할 내용을 작성
       MediaPlayer player = MediaPlayer.create(context.getApplicationContext(),R.raw.play);
       player.start();
    }
}
