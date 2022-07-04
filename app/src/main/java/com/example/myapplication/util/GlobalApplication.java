package com.example.myapplication.util;

import android.app.Application;

import com.example.myapplication.R;
import com.kakao.sdk.common.KakaoSdk;

public class GlobalApplication extends Application {
    private static GlobalApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        KakaoSdk.init(this, getString(R.string.kakao_native_appKey));
    }
}
