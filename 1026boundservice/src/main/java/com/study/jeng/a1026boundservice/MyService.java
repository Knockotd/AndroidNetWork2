package com.study.jeng.a1026boundservice;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class MyService extends Service {
    //ㄷㅔ이터를 전송할 수 있는 Binder 클래스 생성
    class MyLocalBinder extends Binder{
        MyService getService(){
            return MyService.this;
        }
    }
    //Binder 객체를 생성 ==> 다른 서비스는 안 만들지만 이건 바인딩에 등록을 해놔야한다.
    //차라리 이렇게 하는 것 보다 Broadcast의 반송을 버튼 클릭 이벤트 안에 넣어서 많이 사용한다.

    IBinder myBinder = new MyLocalBinder();

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    //서비스를 등록한 곳에서 호출할 메소드
    public String remoteMethod(){
        return "안녕하세요 반갑습니다.";
    }
}
