package com.example.myapplication.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.BroadListAdapter;
import com.example.myapplication.R;

import java.text.SimpleDateFormat;
import java.util.Date;


public class SbsFragment extends Fragment {

    String broadcastStation = "SBS";
    String scheduleDate; //방송일자
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewsbs = inflater.inflate(R.layout.fragment_sbs, container, false);
        RecyclerView rvchat = (RecyclerView) viewsbs.findViewById(R.id.rvchat);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvchat.setLayoutManager(linearLayoutManager);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        scheduleDate = sdf.format(date);
        Log.d("scheduleDate>>>", "" + scheduleDate);


        rvchat.setAdapter(new BroadListAdapter(broadcastStation, scheduleDate));

        return viewsbs;
    }
}