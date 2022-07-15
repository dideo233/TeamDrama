package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.chat.GroupMessageActivity;
import com.example.myapplication.fragment.ChatRoomFragment;
import com.example.myapplication.fragment.MainFragment;
import com.example.myapplication.fragment.MyFragment;
import com.example.myapplication.model.ChatModel;
import com.example.myapplication.model.NoticeData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.nav);

        //처음화면
        getSupportFragmentManager().beginTransaction().add(R.id.change, new MainFragment()).commit(); //FrameLayout에 fragment.xml 띄우기

        //바텀 네비게이션뷰 안의 아이템 설정
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    //item을 클릭시 id값을 가져와 FrameLayout에 fragment.xml띄우기
                    case R.id.Mainpage:
                        getSupportFragmentManager().beginTransaction().replace(R.id.change, new MainFragment()).commit();
                        break;
                    case R.id.Mypage:
                        getSupportFragmentManager().beginTransaction().replace(R.id.change, new MyFragment()).commit();
                        break;
                    case R.id.Seachpage:
                        getSupportFragmentManager().beginTransaction().replace(R.id.change, new ChatRoomFragment()).commit();
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {

        //채팅방 참여 다이얼로그
        Dialog dlgFinish = new Dialog(MainActivity.this);
        dlgFinish.setContentView(R.layout.dialog_logout);
        dlgFinish.show();

        dlgFinish.findViewById(R.id.btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                moveTaskToBack(true); // 태스크를 백그라운드로 이동
                finishAndRemoveTask();
            }
        });
        dlgFinish.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlgFinish.dismiss();
                finish();
            }
        });
    }
}