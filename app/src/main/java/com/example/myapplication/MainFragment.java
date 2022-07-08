package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainFragment extends Fragment {
    ArrayList<TvScheduleData> tvScheduleData = new ArrayList<>();
    ChatListAdapter chatListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        FirebaseDatabase.getInstance().getReference().child("broadcast").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                for(DataSnapshot item : task.getResult()){
//
//                }
//            }
//        });

        chatListAdapter = new ChatListAdapter(tvScheduleData);

        View view = inflater.inflate(R.layout.fragment_main,container,false);
        RecyclerView rvchat = (RecyclerView)view.findViewById(R.id.rvchat);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvchat.setLayoutManager(linearLayoutManager);
        rvchat.setAdapter(chatListAdapter);
        return view;
    }


}