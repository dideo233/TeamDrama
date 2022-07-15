package com.example.myapplication.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.chat.GroupMessageActivity;
import com.example.myapplication.model.ChatModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

///**
// * A simple {@link Fragment} subclass.
// * Use the {@link ChatFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class ChatRoomFragment extends Fragment {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd hh:mm");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.chatfragment_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new ChatRecyclerViewAdapter());

        return view;
    }



    class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private List<ChatModel> chatModelList = new ArrayList<>();
        private List<String> chatroomkeyList = new ArrayList<>(); //방에 대한 키
        private String uid;

        public ChatRecyclerViewAdapter() {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            //채팅방에 속한 user 중에 본인(uid)가 속한 채팅망 리스트를 가져옴
            FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    chatModelList.clear();

                    for(DataSnapshot item : snapshot.getChildren()){
                        chatModelList.add(item.getValue(ChatModel.class)); //채팅방 객체
                        chatroomkeyList.add(item.getKey()); //방에 대한 키
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

//            //채팅방 전체리스트를 가져옴
//            FirebaseDatabase.getInstance().getReference().child("chatrooms").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    chatModelList.clear();
//
//                    for(DataSnapshot item : snapshot.getChildren()){
//                        chatModelList.add(item.getValue(ChatModel.class)); //채팅방 객체
//                        chatroomkeyList.add(item.getKey()); //방에 대한 키
//                    }
//                    notifyDataSetChanged();
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            CustomViewHolder customViewHolder = (CustomViewHolder)holder;
            String destinationUid = null;

            //채팅방 타입
            if(chatModelList.get(position).roomType.equals("비공개")) {
                customViewHolder.textView_roomType.setText(chatModelList.get(position).roomType);
                customViewHolder.textView_roomType.setTextColor(Color.RED);
            } else {
                customViewHolder.textView_roomType.setText(chatModelList.get(position).roomType);
            }

            //채팅방 오픈일
            customViewHolder.textView_openDate.setText(chatModelList.get(position).openDate);
            //방송 타이틀
            customViewHolder.textView_tvScheduleTitle.setText(chatModelList.get(position).tvScheduleTitle);
            //채팅방 제목
            customViewHolder.textView_title.setText(chatModelList.get(position).title);

            //메시지를 내림차순으로 정렬후 마지막 메시지의 키값을 가져옴
            Map<String, ChatModel.Comment> commentMap = new TreeMap<>(Collections.reverseOrder());
            commentMap.putAll(chatModelList.get(position).comments);

            //마지막 메시지 - 메시지가 하나도 없을때(단체방을 처음 개설시) 또는 해당 채팅방 참여자가 아닐때 메시지 표시 생략
            if(commentMap.keySet().toArray().length > 0 && chatModelList.get(position).users.containsKey(uid)){
                String lastMessageKey = (String) commentMap.keySet().toArray()[0];
                customViewHolder.textView_last_message.setText(chatModelList.get(position).comments.get(lastMessageKey).message);
            } else {
                customViewHolder.textView_last_message.setText("마지막 메시지 비공개");
            }

            //채팅방 참여
            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(v.getContext(), GroupMessageActivity.class);
                    String destinationRoom = chatroomkeyList.get(holder.getAdapterPosition());
                    intent.putExtra("destinationRoom", destinationRoom); //방의 키값을 넘김

                    //화면전환 애니메이션효과
                    ActivityOptions activityOptions= null;
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        activityOptions = ActivityOptions.makeCustomAnimation(v.getContext(), R.anim.fromright, R.anim.toleft);
                        startActivity(intent, activityOptions.toBundle());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return chatModelList ==null?0: chatModelList.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {

            public ImageView imageView;
            public TextView textView_openDate;
            public TextView textView_tvScheduleTitle;
            public TextView textView_roomType;
            public TextView textView_title;
            public TextView textView_last_message;

            public CustomViewHolder(View view) {
                super(view);
                imageView = view.findViewById(R.id.chatitem_imageview);
                textView_openDate = view.findViewById(R.id.chatitem_textview_openDate);
                textView_roomType = view.findViewById(R.id.chatitem_textview_roomType);
                textView_tvScheduleTitle = view.findViewById(R.id.chatitem_textview_tvScheduleTitle);
                textView_title = view.findViewById(R.id.chatitem_textview_title);
                textView_last_message = view.findViewById(R.id.chatitem_textview_lastMessage);

            }
        }

    }
}