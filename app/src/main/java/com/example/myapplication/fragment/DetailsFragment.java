package com.example.myapplication.fragment;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.chat.GroupMessageActivity;
import com.example.myapplication.model.ChatModel;
import com.example.myapplication.model.LikeTvScheduleData;
import com.example.myapplication.model.NoticeData;
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
import java.util.TimeZone;

public class DetailsFragment extends Fragment {
    View view;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String myUid = mAuth.getUid();

    TextView tvtitle,tvcategory,tvbroadcastStation;
    String programname; //방송 타이틀
    String programca; //방송분류
    String broadcastStation; //방송국
    String scheduleDate; //방송일자
    String tvScheduleKey; //방송 키값

    Button btncreatechat; //채팅참여 버튼
    ImageButton like; //관심등록 버튼

    RecyclerView recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_details, container, false);

        tvtitle = view.findViewById(R.id.title);
        tvcategory = view.findViewById(R.id.category);
        tvbroadcastStation = view.findViewById(R.id.broadcastStation);
        like = view.findViewById(R.id.like);

        if (getArguments() != null)
        {
            broadcastStation = getArguments().getString("broadcastStation"); //방송국
            scheduleDate = getArguments().getString("scheduleDate"); //방송일자
            programname = getArguments().getString("programname"); // 프래그먼트1에서 받아온 값 넣기
            programca = getArguments().getString("programca");
            tvScheduleKey = getArguments().getString("tvScheduleKey");

            tvtitle.setText(programname);
            tvcategory.setText(programca);
            tvbroadcastStation.setText(broadcastStation);
        }

        //관심등록 여부체크
        FirebaseDatabase.getInstance().getReference().child("member").child(myUid).child("like").child(tvScheduleKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                   like.setImageResource(R.drawable.lovef);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //관심등록  버튼 클릭
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //관심등록 여부체크
                FirebaseDatabase.getInstance().getReference().child("member").child(myUid).child("like").child(tvScheduleKey)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) { //기존에 존재하면
                            //관심등록 삭제
                            like.setImageResource(R.drawable.lovee);
                            FirebaseDatabase.getInstance().getReference().child("member").child(myUid).child("like").child(tvScheduleKey).removeValue();
                        } else {
                            //관심등록 추가
                            LikeTvScheduleData likeTvScheduleData = new LikeTvScheduleData();
                            likeTvScheduleData.setTvScheduleKey(tvScheduleKey);
                            likeTvScheduleData.setBroadcastStation(broadcastStation);
                            likeTvScheduleData.setScheduleDate(scheduleDate);
                            likeTvScheduleData.setProgramname(programname);
                            likeTvScheduleData.setProgramca(programca);
                            FirebaseDatabase.getInstance().getReference().child("member").child(myUid).child("like").child(tvScheduleKey).setValue(likeTvScheduleData).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    like.setImageResource(R.drawable.lovef);
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        //채팅방 리사이클러 뷰
        recyclerView = view.findViewById(R.id.fragment_details_recyclerview);
        recyclerView.setAdapter(new DetailRecyclerViewAdapter());

        //채팅방 개설버튼
        btncreatechat = view.findViewById(R.id.btncreatechat);
        btncreatechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //채팅방개설 다이얼로그
                View dialogView = v.inflate(v.getContext(), R.layout.dialog_room, null);

                EditText edt_title = dialogView.findViewById(R.id.edt_title);
                AlertDialog.Builder dlg = new AlertDialog.Builder(v.getContext());
                dlg.setView(dialogView);
                dialogView.findViewById(R.id.btnPublicTitle).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String title = edt_title.getText().toString();

                        if(title==""){
                            Toast.makeText(getContext(), "방제를 입력해주세요",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ChatModel chatModel = new ChatModel();

                        //날짜
                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        chatModel.openDate = sdf.format(date); //채팅방 오픈일자
                        chatModel.tvScheduleTitle = tvtitle.getText().toString(); //방송 타이틀
                        chatModel.title = title; //채팅방 제목
                        chatModel.tvScheduleKey = tvScheduleKey; //채팅방 키
                        chatModel.roomType = "공개"; //채팅방 타입
                        chatModel.manager = myUid; //방장(채팅방개설자) uid
                        chatModel.users.put(myUid, true); //채팅참여자 방장 추가

                        FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                //채팅방 개설알림
                                long now = System.currentTimeMillis();
                                Date date = new Date(now);
                                SimpleDateFormat sdf = new SimpleDateFormat("kk:mm");
                                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

                                NoticeData noticeData = new NoticeData();
                                noticeData.setType("C"); //채팅방개설
                                noticeData.setTime(sdf.format(date));
                                noticeData.setMessage(chatModel.title + " 공개방 개설" );
                                FirebaseDatabase.getInstance().getReference().child("member").child(myUid).child("notice").push().setValue(noticeData);

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
                dialogView.findViewById(R.id.btnPrimaryTitle).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String title = edt_title.getText().toString();

                        if(title==""){
                            Toast.makeText(getContext(), "방제를 입력해주세요",Toast.LENGTH_SHORT).show();
                            return;
                        }

                        ChatModel chatModel = new ChatModel();

                        //날짜
                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        chatModel.openDate = sdf.format(date); //채팅방 오픈일자
                        chatModel.tvScheduleTitle = tvtitle.getText().toString(); //방송 타이틀
                        chatModel.title = title; //채팅방 제목
                        chatModel.tvScheduleKey = tvScheduleKey;
                        chatModel.roomType = "비공개"; //채팅방 타입
                        chatModel.manager = myUid; //방장(채팅방개설자) uid
                        chatModel.users.put(myUid, true); //채팅참여자 방장 추가

                        FirebaseDatabase.getInstance().getReference().child("chatrooms").push().setValue(chatModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                //채팅방 개설알림
                                long now = System.currentTimeMillis();
                                Date date = new Date(now);
                                SimpleDateFormat sdf = new SimpleDateFormat("kk:mm");
                                sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

                                NoticeData noticeData = new NoticeData();
                                noticeData.setType("C"); //채팅방개설
                                noticeData.setTime(sdf.format(date));
                                noticeData.setMessage(chatModel.title + " 비공개방 개설" );
                                FirebaseDatabase.getInstance().getReference().child("member").child(myUid).child("notice").push().setValue(noticeData);

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
        return view;
    }

    class DetailRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private List<ChatModel> chatModelList = new ArrayList<>(); //채팅방 리스트
        private List<String> chatroomkeyList = new ArrayList<>(); //채팅방 키 리스트

        public DetailRecyclerViewAdapter() {

            //방송에 대한 채팅방 리스트를 가져옴
            FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("tvScheduleKey").equalTo(tvScheduleKey)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot item : snapshot.getChildren()){
                        ChatModel chatModel = item.getValue(ChatModel.class);
                        chatModelList.add(chatModel);
                        chatroomkeyList.add(item.getKey());
                    }

                    //정렬
                    ChatModel temp1 = new ChatModel();
                    String temp2;
                    for(int i = 0; i<chatModelList.size(); i++) {
                        for(int j = i+1; j<chatModelList.size(); j++) {
                            if(chatModelList.get(i).users.size() < chatModelList.get(j).users.size()) {
                                temp1 = chatModelList.get(i);
                                chatModelList.add(i,chatModelList.get(j));
                                chatModelList.add(j, temp1);

                                temp2 = chatroomkeyList.get(i);
                                chatroomkeyList.add(i, chatroomkeyList.get(j));
                                chatroomkeyList.add(j, temp2);
                            }
                        }
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_popular, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

            CustomViewHolder customViewHolder = (CustomViewHolder)holder;

            customViewHolder.itemDetail_textview_rank.setText(Integer.toString(position+1)); //순위
            customViewHolder.itemDetail_textview_title.setText(chatModelList.get(position).title); //채팅방 title

            //채팅참가 버튼 클릭
            customViewHolder.itemDetail_btn_chat.setOnClickListener(new View.OnClickListener() {
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

            TextView itemDetail_textview_rank;
            TextView itemDetail_textview_title;
            Button itemDetail_btn_chat;

            public CustomViewHolder(View view) {
                super(view);
                itemDetail_textview_rank = view.findViewById(R.id.itemDetail_textview_rank);
                itemDetail_textview_title = view.findViewById(R.id.itemDetail_textview_title);
                itemDetail_btn_chat = view.findViewById(R.id.itemDetail_btn_chat);
            }
        }

    }


}