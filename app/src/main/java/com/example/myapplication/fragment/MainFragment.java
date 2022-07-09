package com.example.myapplication.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.BroadListAdapter;
import com.example.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.Date;


public class MainFragment extends Fragment {

    String broadcastStation = "MBC Every1"; //방송사
    String scheduleDate; //방송일자

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main,container,false);
        RecyclerView rvchat = (RecyclerView)view.findViewById(R.id.rvchat);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvchat.setLayoutManager(linearLayoutManager);


        //오늘날짜
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        scheduleDate = sdf.format(date);

        rvchat.setAdapter(new BroadListAdapter(broadcastStation, scheduleDate));
        return view;
    }


}