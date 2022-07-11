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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.LoginActivity;
import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.chat.GroupMessageActivity;
import com.example.myapplication.model.ChatModel;
import com.example.myapplication.model.TvScheduleData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link PeopleFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class TvScheduleFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tvschedule, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.tvschedulefragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new PeopleFragmentRecyclerViewAdapter());

        return view;
    }

    class PeopleFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<TvScheduleData> tvScheduleDataList; //편성표 리스트
        List<String> tvScheduleKeyList; //방송별 키값
        //본인 uid
        String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        public PeopleFragmentRecyclerViewAdapter() {
            tvScheduleDataList = new ArrayList<>();
            tvScheduleKeyList = new ArrayList<>();

            FirebaseDatabase.getInstance().getReference().child("broadcast").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    //데이터가 중복적으로 쌓이는 것을 초기화
                    tvScheduleDataList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        TvScheduleData tvScheduleData = snapshot.getValue(TvScheduleData.class);

                        //편성표 리스트
                        tvScheduleDataList.add(snapshot.getValue(TvScheduleData.class));
                        //방송별 키값
                        tvScheduleKeyList.add(snapshot.getKey());
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

            //***********************************
            // 방송별 포스터 이미지 -----> API 이후 수정해야함.
            //((CustomViewHolder)holder).broadcastItem_imageview.setImageResource(000000000000);
            //***********************************

            ((CustomViewHolder)holder).broadcastItem_textview_time.setText(tvScheduleDataList.get(position).getTime());
            ((CustomViewHolder)holder).broadcastItem_textview_title.setText(tvScheduleDataList.get(position).getTitle());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View dialogView = v.inflate(v.getContext(), R.layout.dialog_room, null);

                    EditText edt_title = dialogView.findViewById(R.id.edt_title);

                    AlertDialog.Builder dlg = new AlertDialog.Builder(v.getContext());
                    dlg.setTitle("채팅방개설");
                    dlg.setView(dialogView);
                    dlg.setPositiveButton("단체방개설", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            String title = edt_title.getText().toString();

                            if(title==""){
                                Toast.makeText(getContext(), "방제를 입력해주세요",Toast.LENGTH_SHORT).show();
                                return;
                            }

                            ChatModel chatModel = new ChatModel();
                            chatModel.title = title;
                            chatModel.tvScheduleKey = tvScheduleKeyList.get(position);
                            chatModel.roomType = "public"; //공개
                            chatModel.users.put(myUid, true);

                            FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //화면전환 효과
                                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        Intent intent = new Intent(getContext(), MainActivity.class);
                                        ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fromright, R.anim.toleft);
                                        startActivity(intent, activityOptions.toBundle());
                                    }
                                }
                            });

                        }
                    });
                    dlg.setNegativeButton("비공개개설", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String title = edt_title.getText().toString();

                            if(title==""){
                                Toast.makeText(getContext(), "방제를 입력해주세요",Toast.LENGTH_SHORT).show();
                                return;
                            }

                            ChatModel chatModel = new ChatModel();
                            chatModel.title = title;
                            chatModel.tvScheduleKey = tvScheduleKeyList.get(position);
                            chatModel.roomType = "private"; //비공개
                            chatModel.users.put(myUid, true);

                            FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //화면전환 효과
                                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                        Intent intent = new Intent(getContext(), MainActivity.class);
                                        ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fromright, R.anim.toleft);
                                        startActivity(intent, activityOptions.toBundle());
                                    }
                                }
                            });
                        }
                    });
                    dlg.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return tvScheduleDataList==null?0:tvScheduleDataList.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView broadcastItem_imageview; //방송포스터이미지
            public TextView broadcastItem_textview_time; //방송시간
            public TextView broadcastItem_textview_title; //방송제목

            public CustomViewHolder(View view) {
                super(view);
                broadcastItem_imageview = view.findViewById(R.id.broadcastItem_imageview);
                broadcastItem_textview_time = view.findViewById(R.id.broadcastItem_textview_time);
                broadcastItem_textview_title = view.findViewById(R.id.broadcastItem_textview_title);
            }
        }
    }
}