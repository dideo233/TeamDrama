package com.example.myapplication.util;

import android.app.Application;

import com.example.myapplication.R;
import com.google.firebase.FirebaseApp;
import com.kakao.sdk.common.KakaoSdk;


//카카오톡 로그인 초기화 코드
public class GlobalApplication extends Application {
    private static GlobalApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        KakaoSdk.init(this, getString(R.string.kakao_native_appKey));
    }
}
