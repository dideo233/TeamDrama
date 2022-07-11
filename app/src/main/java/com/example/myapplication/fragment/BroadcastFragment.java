package com.example.myapplication.fragment;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.chat.GroupMessageActivity;
import com.example.myapplication.model.ChatModel;
import com.example.myapplication.model.TvScheduleData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link PeopleFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class BroadcastFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tvschedule, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.tvschedulefragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new BroadcastFragmentRecyclerViewAdapter());

        return view;
    }

    class BroadcastFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        String broadcastStation = "MBC Every1"; //방송사
        String scheduleDate; //방송일자

        //방송리스트
        List<TvScheduleData> tvScheduleDataList;
        //방송키값
        List<String> tvScheduleKeyList;

        //채팅방 모델객체
        ChatModel chatModel = new ChatModel();

        public BroadcastFragmentRecyclerViewAdapter() {
            tvScheduleDataList = new ArrayList<>();
            tvScheduleKeyList = new ArrayList<>();

            //날짜
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            scheduleDate = sdf.format(date);

            //편성표 리스트
            FirebaseDatabase.getInstance().getReference().child("broadcast").child(broadcastStation).child(scheduleDate).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //데이터가 중복적으로 쌓이는 것을 초기화
                    tvScheduleDataList.clear();

                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        TvScheduleData tvScheduleData = item.getValue(TvScheduleData.class);
                        //방송사리스트
                        tvScheduleDataList.add(item.getValue(TvScheduleData.class));
                        //방송키리스트
                        tvScheduleKeyList.add(item.getKey());
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
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_broadcast, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


            //****************************
            //방송 포스터 이미지 ---> API 완료이후 수정필요
            //((CustomViewHolder)holder).broadcastitem_imageview.setImageResource(00000000000000);
            //*****************************

            ((CustomViewHolder)holder).broadcastitem_textview_time.setText(tvScheduleDataList.get(position).getTime());
            ((CustomViewHolder)holder).broadcastitem_textview_title.setText(tvScheduleDataList.get(position).getTitle());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //본인 uid
                    String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                    //방송 키값
                    String tvScheduleKey = tvScheduleKeyList.get(holder.getAdapterPosition());

                    //단체채팅방 개설
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("채팅방을 개설 하시겠습니까?")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    chatModel.users.put(myUid, true);
                                    chatModel.tvScheduleKey = tvScheduleKey;
                                    FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Intent intent = new Intent(v.getContext(), GroupMessageActivity.class);
                                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fromright, R.anim.toleft);
                                                startActivity(intent, activityOptions.toBundle());
                                            }
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            }).create().show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return tvScheduleDataList==null?0:tvScheduleDataList.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView broadcastitem_imageview; //방송 포스터
            public TextView broadcastitem_textview_time; //방송시간
            public TextView broadcastitem_textview_title; //방송제목

            public CustomViewHolder(View view) {
                super(view);
                broadcastitem_imageview = view.findViewById(R.id.broadcastItem_imageview);
                broadcastitem_textview_time = view.findViewById(R.id.broadcastItem_textview_time);
                broadcastitem_textview_title = view.findViewById(R.id.broadcastItem_textview_title);
            }
        }
    }
}