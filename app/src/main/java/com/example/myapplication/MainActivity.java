package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.myapplication.fragment.MainFragment;
import com.example.myapplication.fragment.MyFragment;
import com.example.myapplication.fragment.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

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
                        getSupportFragmentManager().beginTransaction().replace(R.id.change, new SearchFragment()).commit();
                        break;
                }
                return true;
            }
        });
    }
}