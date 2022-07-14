package com.example.myapplication;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.fragment.DetailsFragment;
import com.example.myapplication.fragment.KbsFragment;
import com.example.myapplication.model.TvScheduleData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BroadListAdapter extends RecyclerView.Adapter<BroadListAdapter.ViewHolder> {

    private ArrayList<TvScheduleData> tvScheduleDataList = new ArrayList<>();
    private List<String> tvScheduleKeyList = new ArrayList<>(); //방에 대한 키
    ArrayList<TvScheduleData> original_list =new ArrayList<>();
    String broadcastStation; //방송사
    String scheduleDate; //방송일자

    public BroadListAdapter(String broadcastStation, String scheduleDate) {
        this.broadcastStation =broadcastStation;
        this.scheduleDate = scheduleDate;

        FirebaseDatabase.getInstance().getReference().child("broadcast").child(broadcastStation).child(scheduleDate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvScheduleDataList.clear();
                for(DataSnapshot item : snapshot.getChildren()){
                    tvScheduleDataList.add(item.getValue(TvScheduleData.class));
                    tvScheduleKeyList.add(item.getKey()); //방에 대한 키
                }
                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mainchatlist, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TvScheduleData tvScheduleData = tvScheduleDataList.get(position);
        String tvnamesub = tvScheduleData.getTitle();
        if (tvnamesub.length() >= 20){
            tvnamesub=tvnamesub.substring(0,17)+"...";
        }
        holder.programname.setText(tvnamesub);
        holder.programep.setText(tvScheduleData.getTime());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = view.getContext();
                // 번들을 통해 값 전달
                Bundle bundle = new Bundle();
                int PPosition = holder.getAdapterPosition();
                bundle.putString("broadcastStation", broadcastStation); //방송국
                bundle.putString("scheduleDate", scheduleDate); //방송일자
                bundle.putString("programname", tvScheduleDataList.get(PPosition).getTitle()); //방송제목
                bundle.putString("programca", tvScheduleDataList.get(PPosition).getCategory()); //방송분류
                bundle.putString("tvScheduleKey", tvScheduleKeyList.get(PPosition)); // 프로그램 키값
                FragmentTransaction transaction = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
                DetailsFragment programFrag = new DetailsFragment();//프래그먼트2 선언
                programFrag.setArguments(bundle);//번들을 프래그먼트2로 보낼 준비
                transaction.replace(R.id.change,programFrag);
                transaction.commit();

            }
        });

    }

    @Override
    public int getItemCount() {
        return tvScheduleDataList ==null?0: tvScheduleDataList.size();
    }
    public void setItems(ArrayList<TvScheduleData> list){
        tvScheduleDataList = list;
        notifyDataSetChanged();
    }

    public ArrayList<TvScheduleData> getTvScheduleDataList() {
        notifyDataSetChanged();
        return tvScheduleDataList;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView programname, programep;
        ImageButton programdetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            programname = itemView.findViewById(R.id.programname);
            programep = itemView.findViewById(R.id.programep);
            programdetails = itemView.findViewById(R.id.prgramdetails);
        }
    }
}
