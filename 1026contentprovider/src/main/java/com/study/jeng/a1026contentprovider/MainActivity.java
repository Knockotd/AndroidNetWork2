package com.study.jeng.a1026contentprovider;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //버튼
    Button contactBtn, gallrayBtn;
    //레이아웃
    LinearLayout mainContent;
    //이미지 출력 크기를 위한 변수
    int reqWidth, reqHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //디자인 한 위젯 찾아오기
        contactBtn = (Button) findViewById(R.id.lab2_contacts);
        gallrayBtn = (Button) findViewById(R.id.lab2_gallery);
        mainContent = (LinearLayout) findViewById(R.id.lab2_content);

        //버튼의 클릭 이벤트를 처리할 객체 설정
        contactBtn.setOnClickListener(this);
        gallrayBtn.setOnClickListener(this);

        //디바이스의 너비와 높이를 가져와서 변수에 대입
        DisplayMetrics dm = getResources().getDisplayMetrics();
        reqWidth = dm.widthPixels;
        reqHeight = dm.heightPixels;

        //os가 6.0 이상일 때 동적인 권한 요청 - 연락처와 외부저장장치 관련
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
    }

    //버튼의 클릭 이벤트 처리 메소드
    public void onClick(View v) {
        //연락처 클릭
        /*if(v == contactBtn){

        }*/
        //인스턴스 변수가 아닐 땐 이렇게 해야한다.
        if (v.getId() == R.id.lab2_contacts) {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setData(Uri.parse("content://com.android.contacts/data/phones"));
            //액티비티를 화면에 출력하고 출력된 액티비티가 종료되면 10이라는 값과
            //onActivituResult를 메소드를 호출합니다.
            startActivityForResult(intent, 10);
        }
        //갤러리 앱에서 가져오기(갤러리 앱 실행)
        else if (v == gallrayBtn) {
            //Log.e("갤러리 앱:","갤러리앱 실행");
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 20);
            //Log.e("갤러리 앱:","" + 20);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e("tag0 : 메소드:","0");
        //연락처 앱에서 확인을 눌렀다면
        if (requestCode == 10 && resultCode == RESULT_OK) {
            //선택한 데이터 가져오기
            String id =
                    Uri.parse(data.getDataString()).getLastPathSegment();
            Cursor cursor = getContentResolver().query(
                    ContactsContract.Data.CONTENT_URI,
                    new String[]{ContactsContract.Contacts.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER},
                    ContactsContract.Contacts._ID + "=" + id,
                    null, null);
            //첫번째 행으로 이동
            cursor.moveToNext();
            String name = cursor.getString(0);
            String number = cursor.getString(1);

            //동적으로 TextView를 만들어서 화면에 추가
            TextView textView = new TextView(this);
            textView.setText(name + ":" + number);
            textView.setTextSize(28);
            //크기 만들기
            LinearLayout.LayoutParams params =
                    new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            mainContent.addView(textView, params);


        } else if(requestCode == 20 && resultCode==RESULT_OK){
           //갤러리 앱의 URI
            Uri uri = data.getData();
            //선택한 이미지 파일의 파일 경로 만들기
           String filepath = getFilePathFromDocumentUrl(this, uri);
           Log.e("파일 경로", filepath);
           //이미지 뷰에 추가
           insertImageView(filepath);
        }
    }

    //onCreate에서 부를 용도로 내가 직접 만드는 메소드라서 private
    private void insertImageView(String filepath) {
        //파일 경로가 없다면
        if (filepath.equals("") == false) {
            //파일 객체 생성
            File file = new File(filepath);
            //이미지를 확대 축소 하기 위한 객체 생성
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            try {
                //파일의 내용을 읽어서 Bitmap으로 만들기

                InputStream in = new FileInputStream(file);
                BitmapFactory.decodeStream(in, null, options);
                in.close();
                in = null;
            } catch (Exception e) {
                Log.e("이미지 파일 읽기 실패", e.getMessage());
            }
            int width = options.outWidth;
            int sampleSize = 1;
            //  이미지의 크기가 화면 크기 보다 크다면 이미지를 축소하기 위해서 나누기를 해서 그 배울을 simpleSize에 대입.
            //실수로 변환해서 나누기 한 후 반올림
            if (width > reqWidth) {
                sampleSize = Math.round((float) width / (float) reqWidth);
            }

            //확대 축소 배율을 적용해서 이미지를 다시 읽기
            BitmapFactory.Options imgOptions =
                    new BitmapFactory.Options();
            imgOptions.inSampleSize = sampleSize;
            Bitmap bitmap = BitmapFactory.decodeFile(filepath, imgOptions);


            //Bitmap을 ImageView를 이용해서 출력
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(bitmap);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            mainContent.addView(imageView, params);


        }

    }

    //이미지 파일의 경로를 가져오는 메소드
    //이미지 파일은 image:파일명의 형식
    private String getFilePathFromDocumentUrl(Context context, Uri uri) {
        String docId = DocumentsContract.getDocumentId(uri);
        String[] split = docId.split(":");
        String type = split[0];
        Uri contentUri = null;
        //NullPointerException을 방지하기 위해서 상수를 가지고 비교
        //상수를 equals 앞에 놓는 게 더 좋다. 변수가 null인 경우에도 예외가 발생하지 않는다.
        if ("image".equals(type)) {
            contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }
        //이미지 파일의 ID 가져오기
        String selection = MediaStore.Images.Media._ID + "=?";
        String [] selectionArg = new String[]{split[1]};

        String column = "_data";
        String [] projection = {column};

        //ContentProvider에서 데이터 찾아오기
        Cursor cursor = context.getContentResolver().query(contentUri,projection,selection,selectionArg,null);
        //Cursor의 데이터 읽기
        String filepath = null;
        if(cursor != null && cursor.moveToFirst()){
            int column_index = cursor.getColumnIndexOrThrow(column);
            filepath = cursor.getString(column_index);
        }
        cursor.close();
        return filepath;
    }

}
