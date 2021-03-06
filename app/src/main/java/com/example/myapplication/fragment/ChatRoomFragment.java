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
        private List<String> chatroomkeyList = new ArrayList<>(); //?????? ?????? ???
        private String uid;

        public ChatRecyclerViewAdapter() {
            uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            //???????????? ?????? user ?????? ??????(uid)??? ?????? ????????? ???????????? ?????????
            FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/"+uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    chatModelList.clear();

                    for(DataSnapshot item : snapshot.getChildren()){
                        chatModelList.add(item.getValue(ChatModel.class)); //????????? ??????
                        chatroomkeyList.add(item.getKey()); //?????? ?????? ???
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

//            //????????? ?????????????????? ?????????
//            FirebaseDatabase.getInstance().getReference().child("chatrooms").addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    chatModelList.clear();
//
//                    for(DataSnapshot item : snapshot.getChildren()){
//                        chatModelList.add(item.getValue(ChatModel.class)); //????????? ??????
//                        chatroomkeyList.add(item.getKey()); //?????? ?????? ???
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatroom, parent, false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            CustomViewHolder customViewHolder = (CustomViewHolder)holder;
            String destinationUid = null;

            //????????? ??????
            if(chatModelList.get(position).roomType.equals("?????????")) {
                customViewHolder.imageView_roomType.setImageResource(R.drawable.hide);
            } else {
                customViewHolder.imageView_roomType.setImageResource(R.drawable.nothide);
            }

            //????????? ?????????
            customViewHolder.textView_openDate.setText(chatModelList.get(position).openDate);

            //?????? ?????????
            String tvScheduleTitlesub = chatModelList.get(position).tvScheduleTitle;
            if (tvScheduleTitlesub.length() >20){
                tvScheduleTitlesub=tvScheduleTitlesub.substring(0,19)+"...";
            }
            customViewHolder.textView_tvScheduleTitle.setText("?????? : "+tvScheduleTitlesub);

            //????????? ??????
            String titlesub = chatModelList.get(position).title;
            if (titlesub.length() >21){
                titlesub=titlesub.substring(0,20)+"...";
            }
            customViewHolder.textView_title.setText(titlesub);

            //???????????? ?????????????????? ????????? ????????? ???????????? ????????? ?????????
            Map<String, ChatModel.Comment> commentMap = new TreeMap<>(Collections.reverseOrder());
            commentMap.putAll(chatModelList.get(position).comments);

            //????????? ????????? - ???????????? ????????? ?????????(???????????? ?????? ?????????) ?????? ?????? ????????? ???????????? ????????? ????????? ?????? ??????
            if(commentMap.keySet().toArray().length > 0 && chatModelList.get(position).users.containsKey(uid)){
                String lastMessageKey = (String) commentMap.keySet().toArray()[0];
                String lastMessagesub = chatModelList.get(position).comments.get(lastMessageKey).message;
                if (lastMessagesub.length() >21){
                    lastMessagesub=lastMessagesub.substring(0,20)+"...";
                }
                customViewHolder.textView_last_message.setText(lastMessagesub);
            } else {
                customViewHolder.textView_last_message.setText("");
            }

            //????????? ??????
            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(v.getContext(), GroupMessageActivity.class);
                    String destinationRoom = chatroomkeyList.get(holder.getAdapterPosition());
                    intent.putExtra("destinationRoom", destinationRoom); //?????? ????????? ??????

                    //???????????? ?????????????????????
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

            public TextView textView_openDate;
            public TextView textView_tvScheduleTitle;
            public ImageView imageView_roomType;
            public TextView textView_title;
            public TextView textView_last_message;

            public CustomViewHolder(View view) {
                super(view);
                textView_openDate = view.findViewById(R.id.chatitem_textview_openDate);
                imageView_roomType = view.findViewById(R.id.chatitem_imageview_roomType);
                textView_tvScheduleTitle = view.findViewById(R.id.chatitem_textview_tvScheduleTitle);
                textView_title = view.findViewById(R.id.chatitem_textview_title);
                textView_last_message = view.findViewById(R.id.chatitem_textview_lastMessage);

            }
        }

    }
}