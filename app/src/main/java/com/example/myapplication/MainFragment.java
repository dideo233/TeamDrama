package com.example.myapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.myapplication.model.TvScheduleData;
import com.example.myapplication.util.Crawler;

import java.util.ArrayList;

public class MainFragment extends Fragment {
   // ArrayList<TvScheduleData> tvScheduleData;
    ChatListAdapter chatListAdapter;
    Crawler crawler;
    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         /*
        Main 쓰레드에서 네트워크 연결을 하면 나타나는 에러 발생.
        메인 쓰레드에서 네트워크 호출을 하게되면 화면이 응답을 기다리는 동안 화면이 멈춰버리게 되므로 에러를 발생시킨다
        */

        crawler = new Crawler();
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


        Log.d("onCreate", "onCreate()");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main,container,false);
        RecyclerView rvchat = (RecyclerView)view.findViewById(R.id.rvchat);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvchat.setLayoutManager(linearLayoutManager);
        rvchat.setAdapter(chatListAdapter);
        return view;
    }


}