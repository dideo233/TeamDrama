package com.example.myapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.myapplication.util.Crawler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

public class SplashActivity extends AppCompatActivity {

    private LinearLayout linearLayout;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private Crawler crawler = new Crawler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Thread() {
            public void run() {
                try {
                    Log.d("크롤러 실행 : ", "..");
                    crawler.tvScheduleParse();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        //상태바 없애기
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        linearLayout = findViewById(R.id.splashactivity_linearlayout);
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(0)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.default_config);

        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if(task.isSuccessful()) {
                            boolean updated = task.getResult();
                            Log.d(TAG, "Config params updated: " + updated);
                            Toast.makeText(SplashActivity.this, "Fetch and activate succeeded",
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(SplashActivity.this, "Fetch failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                        displayMessage();
                    }
                });
        }

        void displayMessage(){
            String splash_background = mFirebaseRemoteConfig.getString("splash_background");
            boolean caps = mFirebaseRemoteConfig.getBoolean("splash_message_caps");
            String splash_message = mFirebaseRemoteConfig.getString("splash_message");

            linearLayout.setBackgroundColor(Color.parseColor(splash_background));

            if(caps){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(splash_message);
                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });

                builder.create().show();

            } else { //로그인페이지로 이동
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }

        }

    @Override
    protected void onStart() {
        super.onStart();
        /*
        Main 쓰레드에서 네트워크 연결을 하면 나타나는 에러 발생.
        메인 쓰레드에서 네트워크 호출을 하게되면 화면이 응답을 기다리는 동안 화면이 멈춰버리게 되므로 에러를 발생시킨다
        */



    }

}