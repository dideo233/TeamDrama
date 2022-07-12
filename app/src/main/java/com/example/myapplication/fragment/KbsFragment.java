package com.example.myapplication.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.BroadListAdapter;
import com.example.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class KbsFragment extends Fragment {

    String broadcastStation = "KBS2";
    String scheduleDate; //방송일자
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewkbs = inflater.inflate(R.layout.fragment_kbs, container, false);
        RecyclerView rvchat = (RecyclerView) viewkbs.findViewById(R.id.rvchat);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvchat.setLayoutManager(linearLayoutManager);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        scheduleDate = sdf.format(date);
        Log.d("scheduleDate>>>", "" + scheduleDate);


        rvchat.setAdapter(new BroadListAdapter(broadcastStation, scheduleDate));

        return viewkbs;
    }
}