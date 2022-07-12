package com.example.myapplication.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapplication.BroadListAdapter;
import com.example.myapplication.R;
import com.example.myapplication.model.TvScheduleData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MbceveryFragment extends Fragment {

    String broadcastStation = "MBC Every1";
    String scheduleDate; //방송일자

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewme = inflater.inflate(R.layout.fragment_mbcevery, container, false);
        RecyclerView rvchat = (RecyclerView)viewme.findViewById(R.id.rvchat);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvchat.setLayoutManager(linearLayoutManager);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        scheduleDate = sdf.format(date);
        Log.d("scheduleDate>>>",""+scheduleDate );


        rvchat.setAdapter(new BroadListAdapter(broadcastStation, scheduleDate));

        return viewme;
    }
}