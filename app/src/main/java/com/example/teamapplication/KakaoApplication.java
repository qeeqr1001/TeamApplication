package com.example.teamapplication;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class KakaoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        KakaoSdk.init(this,"5041245e68adc7573c3ff192c7169148");

    }
}
