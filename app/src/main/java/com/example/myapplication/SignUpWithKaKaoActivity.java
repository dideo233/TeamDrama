package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.myapplication.model.UserModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


//카카오 ooath2로 회원가입할 때, firebase를 이용한다면 access token으로 custom token을 만들어야한다.
//그 과정 요약
//1.access token 발급
//2.서버에서 kakao api에 access token을 넘겨 사용자 정보 획득
//3.성공적으로 user 데이터를 받아오면 firebase admin sdk로 firebase auth에 user를 생성
//4.이렇게 생성된 user의 uid로 firebase custom token 생성.
//5.Firebase Auth에서 제공하는 signInWithCustomToken 메서드 인자로 custom token을 넘겨 로그인 처리
public class SignUpWithKaKaoActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_with_ka_kao_activiry);
        mAuth = FirebaseAuth.getInstance();
        login();
    }

    //로그인 진행할 때 timeout, 지연 문제 fix해야 함
    public void login(){
        String TAG = "login()";
        if(UserApiClient.getInstance().isKakaoTalkLoginAvailable(SignUpWithKaKaoActivity.this)){ //카카오톡 로그인
            Log.d("kakao talk login ", "....");
        } else { //카카오 계정 로그인
            Log.d("kakao acount login ", "....");
            UserApiClient.getInstance().loginWithKakaoAccount(SignUpWithKaKaoActivity.this, (OAuthToken oAuthToken, Throwable error) -> {
                if (error != null) {
                    Log.e(TAG, "카카오톡 로그인 실패", error);
                } else if (oAuthToken != null) {
                    Log.i(TAG, "카카오록 로그인 성공(access token) : " + oAuthToken.getAccessToken());
                    Log.i(TAG, "firebase auth createing : " + "...");
                    //getUserInfo();

                    getFirebaseJwt(oAuthToken.getAccessToken()).continueWithTask(new Continuation<String, Task<AuthResult>>() {
                       @Override
                        public Task<AuthResult> then(@NonNull Task<String> task) throws Exception {
                            String firebaseToken = task.getResult();
                            return mAuth.signInWithCustomToken(firebaseToken); //firebase 로그인 인증
                        }
                    }).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) { //firebase 로그인 성공
                                Log.d("firebaseAuthWithKaKao", "Success");
                                FirebaseUser user = mAuth.getCurrentUser();

                                UserModel userModel = new UserModel();
                                userModel.setUid(user.getUid()); //Auth uid
                                userModel.setEmail(user.getProviderData().get(0).getEmail()); //아이디(이메일)
                                userModel.setNickName(user.getDisplayName()); //닉네임
                                userModel.setLoginKind(user.getProviderData().get(0).getProviderId()); //로그인 유형

                                Log.d("custom size ", user.getProviderId().length()+"");
                                //member아래 UID를 child 키로 user객체를 저장
                                FirebaseDatabase.getInstance().getReference().child("member").child(user.getUid()).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d("FirebaseDatabase ::: ","회원가입성공 : " + user.getEmail());
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("FirebaseDatabase ::: ", "회원가입 데이터입력에러");
                                            }
                                        });
                                finish();
                            } else { //firebase 로그인 실패
                                Log.d("firebaseAuthWithKaKao ", "Failed");
                                if (task.getException() != null) {
                                    Log.e(TAG, task.getException().toString());
                                }
                            }
                        }
                    });
                }
                return null;
            });
        }
    }

    //카톡 로그인 유저 정보 확인
     public void getUserInfo(){
        String TAG = "getUserInfo()";
        UserApiClient.getInstance().me((user, meError) -> {
            if (meError != null) {
                Log.e(TAG, "사용자 정보 요청 실패", meError);
            } else {
                System.out.println("로그인 완료");
                Log.i(TAG, user.toString());
            }
            return null;
        });
    }
    
    //Jwt 토큰 생성하기 위해 access token과 함께 요청을 날리는 메소드 (해당 기능 로컬 서버 만들어서 구현함)
    private Task<String> getFirebaseJwt(final String kakaoAccessToken) {
        final TaskCompletionSource<String> source = new TaskCompletionSource<>();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = getResources().getString(R.string.validation_server_domain) + "/verifyToken";
        HashMap<String, String> validationObject = new HashMap<>();
        validationObject.put("token", kakaoAccessToken);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(validationObject), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String firebaseToken = response.getString("firebase_token");
                    source.setResult(firebaseToken); 
                    Log.d("success ", "토큰 반환 받았음");
                    Log.d("firebase token : ", firebaseToken);

                } catch (Exception e) {
                    source.setException(e);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley  ERROR", error.toString());
                source.setException(error);
            }
        });
        queue.add(request);
        return source.getTask();
    }
}