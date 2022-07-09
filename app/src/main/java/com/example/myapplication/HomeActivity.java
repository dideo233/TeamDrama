package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.util.Crawler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


//0.회원가입 시에 닉네임 설정할 수 있도록 하기
// -> DB에 저장될 값 : nickname
// -> 구글 가입 시 단말기에 구글 계정 로그인된 상태면 바로 가입될 때 거칠 중간 액티비티 만들기.
// 구글 가입 클릭 -> 계속 진행하시겠습니까? (!) -> 닉네임 설정 화면 (!) -> 회원 가입

//로그인 성공 페이지
//1.네비케이션 메뉴 생성
//-> 프로필 버튼 생성 (0번 안 된 상황에서는 이메일 보여주게 하기)
//-> 로그아웃 버튼 만들기
//2.네비게이션 메뉴 만들면 닉네임 확인은 바로 가능
public class HomeActivity extends AppCompatActivity {
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            finish();
            return;
        }

        //Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        //startActivity(intent);


//        //대충 테스트
//        Log.d("provider Data :",user.getProviderData().toString());
//        for(UserInfo profile : user.getProviderData()){
//            Log.d("pro : ", profile.getProviderId());
//            Log.d("Provider-specific UID:", profile.getEmail());
//            Log.d("Name:", profile.getEmail());
//        }
//
//        TextView tvUserEmail = findViewById(R.id.tvUserEmail);
//        tvUserEmail.setText(user.getEmail());
//        TextView tvUserProvider = findViewById(R.id.tvUserProvider);
//        tvUserProvider.setText(user.getProviderData().get(1).getProviderId());
//        TextView tvDisplayName = findViewById(R.id.tvDisplayName);
//        tvDisplayName.setText(user.getDisplayName());

        Button btnLogout = findViewById(R.id.btnLogout);
        //로그아웃 (* 리스너?)
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

//        //구글 계정 연결 끈기
//        Button btnSignout = findViewById(R.id.btnSignout);
//        btnSignout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //다른 액티비티의 메소드를 호출하기 위해 new 키워드로 액티비티 생성하는 것은 불가
//                //https://soo0100.tistory.com/1266 참고
//
//                ((SignUpWithGoogleActivity)SignUpWithGoogleActivity.mContext).revokeAccess();
//                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                startActivity(intent);
//            }
//        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        /*
        Main 쓰레드에서 네트워크 연결을 하면 나타나는 에러 발생.
        메인 쓰레드에서 네트워크 호출을 하게되면 화면이 응답을 기다리는 동안 화면이 멈춰버리게 되므로 에러를 발생시킨다
        */

        Crawler crawler = new Crawler();
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

    }
}