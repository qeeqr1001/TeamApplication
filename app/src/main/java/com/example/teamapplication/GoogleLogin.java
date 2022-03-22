package com.example.teamapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class GoogleLogin extends AppCompatActivity {

    private TextView tv_result; //닉네임 text
    private ImageView iv_profile; //이미지 뷰



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);

        Intent intent=getIntent();
        String nickName=intent.getStringExtra("nickName");
        String photoUrl=intent.getStringExtra("photoUrl");
        tv_result=findViewById(R.id.tv_result);
        tv_result.setText(nickName); //닉네임text를 텍스트뷰에 세팅

       iv_profile=findViewById(R.id.iv_profile);
       Glide.with(this).load(photoUrl).into(iv_profile); //프로필 url을 이미지뷰에 세팅

    }
}