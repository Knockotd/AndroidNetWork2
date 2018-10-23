package com.study.jeng.androidnetwork;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownload extends AppCompatActivity {
    Button print;
    Button download;
    ImageView show;

Handler printHandler = new Handler(){
    @Override
    public void handleMessage(Message msg){
        Bitmap bitmap = (Bitmap)msg.obj;
        show.setImageBitmap(bitmap);
    }
};

Handler downHendler = new Handler(){
  @Override
  public void handleMessage(Message message){
      if(message.obj != null){
          //파일이 존재하는 경우
          //안드로이드의 data 디렉토리 경로를 가져오기
          String path = Environment.getDataDirectory().getAbsolutePath();
          //현재 앱 내의 파일 경로 만들기
          path = path + "/data/com.study.jeng.androidnetwork/files/" + (String)message.obj;
          //이미지 파일을 show에 출력
          show.setImageBitmap(BitmapFactory.decodeFile(path));

      }else{
          //파일이 존재하지 않는 경우
          Toast.makeText(ImageDownload.this, "파일이 존재하지 않음",Toast.LENGTH_SHORT).show();
      }
  }
};
//이미지를 다운로드 받아서 파일로 저장하는 메소드
class DownloadeThread extends Thread{
    String addr;
    String filepath;
    public DownloadeThread(String addr, String filepath){
        this.addr = addr;
        this.filepath = filepath;
    }
    @Override
    public void run(){
        try{
            URL url = new URL(addr);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setConnectTimeout(20000);
            con.setUseCaches(false);
            //내용을 읽을 스트림을 생성
            InputStream is = con.getInputStream();
            //기록할 스트림 생성
            PrintStream ps = new PrintStream(openFileOutput(filepath,0));
            Log.e("스트림",ps.toString());
            //is에서 읽어서 ps에 기록
            while(true){
                Log.e("데이터:","읽고 있나?");
                byte [] b = new byte[1024];
                int read = is.read(b);
                if(read <= 0){
                    break;
                }
                ps.write(b, 0, read);
            }
            Log.e("기록","/기록완료/"+ ps.toString());
            is.close();
            ps.close();
            con.disconnect();

            Message msg = new Message();
            msg.obj = filepath;
            downHendler.sendMessage(msg);

        }catch (Exception e){
            Log.e("DownloadThread 오류",e.getMessage());
        }
    }
}

class PrintThread extends Thread{
    @Override
    public void run(){
        try{
            String addr = "https://i.imgur.com/ISa6wI6.gif";
            URL url = new URL(addr);
            //url에 연결해서 비트맵 만들기
            Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());
            //비트맵을 핸들러에게 전달
            Message msg = new Message();
            msg.obj = bitmap;
            printHandler.sendMessage(msg);
        }catch (Exception e){
            Log.e("스레드 오류",e.getMessage());
        }
    }
}


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_download);

        print = (Button)findViewById(R.id.print);
        download = (Button)findViewById(R.id.download);
        show = (ImageView)findViewById(R.id.show);

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintThread pt = new PrintThread();
                pt.start();
            }
        });

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //이미지를 다운로드 받을 주소
                String imageURL = "https://image-notepet.akamaized.net/resize/620x-/card_news/201803/bdbc82a164dc20d04794cd269939f32e.jpg";
                //파일명 만들기
                int idx = imageURL.lastIndexOf("/");
                String filename = imageURL.substring(idx + 1);

                //파일 경로 만들기
                String data =  Environment.getDataDirectory().getAbsolutePath();
                String path = data + "/data/com.study.jeng.androidnetwork/files/" + filename;
                //파일 존재 여부를 확인
                if(new File(path).exists()){
                    Toast.makeText(ImageDownload.this,"파일이 존재함",Toast.LENGTH_SHORT).show();
                    show.setImageBitmap(BitmapFactory.decodeFile(path));
                }else{
                    Toast.makeText(ImageDownload.this,"파일이 존재하지 않음",Toast.LENGTH_SHORT).show();
                    //넘겨준 이미지 파일 경로와 파일이름 확인
                    DownloadeThread dt = new DownloadeThread(imageURL,filename);
                    dt.start();
                }


            }
        });

    }
}
