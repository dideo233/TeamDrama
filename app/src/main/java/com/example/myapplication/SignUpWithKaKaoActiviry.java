package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.Account;

//에뮬레이터에 카톡 설치? 일단 거르기

//카카오 ooath2로 회원가입할 때, firebase를 이용한다면 access token으로 custom token을 만들어야한다.
//그 과정 요약
//1.access token 발급
//2.서버에서 kakao api에 access token을 넘겨 사용자 정보 획득
//3.성공적으로 user 데이터를 받아오면 firebase admin sdk로 firebase auth에 user를 생성
//4.이렇게 생성된 user의 uid로 firebase custom token 생성.
//5.Firebase Auth에서 제공하는 signInWithCustomToken 메서드 인자로 custom token을 넘겨 로그인 처리
public class SignUpWithKaKaoActiviry extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_with_ka_kao_activiry);
        login();
    }


    public void login(){
        String TAG = "login()";
        UserApiClient.getInstance().loginWithKakaoTalk(SignUpWithKaKaoActiviry.this,(oAuthToken, error) -> {
            if (error != null) {
                Log.e(TAG, "로그인 실패", error); //휴대폰 단말기에 카톡 설치가 되어 있어야함
            } else if (oAuthToken != null) {
                Log.i(TAG, "로그인 성공(토큰) : " + oAuthToken.getAccessToken());
                getUserInfo();
            }
            return null;
        });
    }
    
    public void getUserInfo(){
        String TAG = "getUserInfo()";
        UserApiClient.getInstance().me((user, meError) -> {
            if (meError != null) {
                Log.e(TAG, "사용자 정보 요청 실패", meError);
            } else {
                System.out.println("로그인 완료");
                Log.i(TAG, user.toString());
                {
                    Log.i(TAG, "사용자 정보 요청 성공" +
                            "\n회원번호: "+user.getId());
                }
                Account user1 = user.getKakaoAccount();
                System.out.println("사용자 계정" + user1);
            }
            return null;
        });
    }

    /*
    public void accountLogin(){
        String TAG = "accountLogin()";
        UserApiClient.getInstance().loginWithKakaoAccount(SignUpWithKaKaoActiviry.this,(oAuthToken, error) -> {
            if (error != null) {
                Log.e(TAG, "로그인 실패", error);
            } else if (oAuthToken != null) {
                Log.i(TAG, "로그인 성공(토큰) : " + oAuthToken.getAccessToken());
                getUserInfo();
            }
            return null;
        });
    }
    */
}