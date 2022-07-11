package com.example.myapplication.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.os.Bundle;

import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;
import com.example.myapplication.model.ChatModel;
import com.example.myapplication.model.UserModel;
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

public class GroupMessageActivity extends AppCompatActivity {
    Map<String,UserModel> members = new HashMap<>();

    String destinationRoom; //채팅방 구분키
    ChatModel chatModel; //채팅방 정보
    String managerUid; //채팅방 방장 uid

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String uid = firebaseAuth.getCurrentUser().getUid(); //현재 접속 유저
    EditText editText;
    Button button;

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private RecyclerView recyclerView;

    List<ChatModel.Comment> commentList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message);

        //메시지입력란
        editText = findViewById(R.id.groupMessageActivity_editText);
        button = findViewById(R.id.groupMessageActivity_button);

        //채팅방 구분 키
        destinationRoom = getIntent().getStringExtra("destinationRoom");

        //채팅방, 방장 uid 정보
        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatModel = snapshot.getValue(ChatModel.class);
                managerUid = chatModel.manager;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //방장이 아닌경우 채팅방 참여권한체크
        if(!uid.equals(managerUid)) {
            checkUser();
        }

        //사용자 목록
        FirebaseDatabase.getInstance().getReference().child("member").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot item : dataSnapshot.getChildren()){
                    members.put(item.getKey(),item.getValue(UserModel.class));
                }

                //참여권한 체크 및 버튼 초기화
                init();

                recyclerView = findViewById(R.id.groupMessageActivity_recyclerview);
                recyclerView.setLayoutManager(new LinearLayoutManager(GroupMessageActivity.this));
                recyclerView.setAdapter(new GroupMessageRecyclerViewAdapter());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    //초기화
    void init(){

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //방장이 아닌경우 채팅방 참여권한체크
                if(!uid.equals(managerUid)) {
                    checkUser();
                }

                //메시지 입력
                ChatModel.Comment comment = new ChatModel.Comment();
                comment.uid = uid;
                comment.message = editText.getText().toString();
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        editText.setText("");
                    }
                });
            }
        });
    }

    //채팅방 참여권한 체크
    void checkUser() {
        //채팅참여자 조회
        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) { //채팅참여자 등록
                            //채팅방 타입에 따라 채팅참여자 초기권한 제한
                            if (chatModel.roomType.equals("공개")) { //채팅방이 공개인경우
                                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("users").child(uid).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                       Toast.makeText(getApplicationContext(), chatModel.title + "방에 오신것을 환영합니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else if (chatModel.roomType.equals("비공개")) { //채팅방이 비공개인 경우
                                //채팅방 참여 다이얼로그
                                AlertDialog.Builder dlg = new AlertDialog.Builder(GroupMessageActivity.this);
                                dlg.setTitle("채팅참여요청");
                                dlg.setMessage("비공개 채팅방입니다. 채팅참여를 요청하시겠습니까?");
                                dlg.setPositiveButton("요청", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //메시지 입력
                                        ChatModel.Comment comment = new ChatModel.Comment();
                                        comment.uid = uid;
                                        comment.message = "방장님 " + firebaseAuth.getCurrentUser().getEmail() + " 채팅참여 요청(클릭후 수락여부 확인)";
                                        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                                    }
                                });
                                dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                });
                                dlg.create().show();
                            }
                        } else { //채팅참여대상인 경우
                            Toast.makeText(getApplicationContext(), chatModel.title + "방에 오신것을 환영합니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    class GroupMessageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        public GroupMessageRecyclerViewAdapter(){
            getMessageList();
        }

        //해당 채팅방 메시지 리스트 받기
        void getMessageList() {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("comments");
            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    commentList.clear();
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        ChatModel.Comment comment = item.getValue(ChatModel.Comment.class);
                        commentList.add(comment);
                    }
                    //메시지가 없는 경우
                    if(commentList.size() == 0){
                        return;
                    }

                    notifyDataSetChanged();

                    //리사이클러뷰 스크롤을 맨마지막으로
                    recyclerView.scrollToPosition(commentList.size() - 1);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false);

            return new GroupMessageViewHodler(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            GroupMessageViewHodler messageViewHolder = ((GroupMessageViewHodler) holder);

            //내가보낸 메세지
            if (commentList.get(position).uid.equals(uid)) {
                if(commentList.get(position).uid.equals(managerUid)) { //방장 표시
                    messageViewHolder.messageItem_textview_nickName.setText("[방장]");
                }
                messageViewHolder.messageItem_textView_message.setText(commentList.get(position).message);
                messageViewHolder.messageItem_textView_message.setBackgroundResource(R.drawable.rightbubble);
                messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
                messageViewHolder.messageItem_textView_message.setTextSize(20);
                messageViewHolder.linearLayout_main.setGravity(Gravity.END);
            } else { //상대방이 보낸 메세지
                if(commentList.get(position).uid.equals(managerUid)) { //방장표시
                    messageViewHolder.messageItem_textview_nickName.setText("[방장] "+members.get(commentList.get(position).uid).getNickName());
                } else {
                    messageViewHolder.messageItem_textview_nickName.setText(members.get(commentList.get(position).uid).getNickName());
                }
                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.messageItem_textView_message.setBackgroundResource(R.drawable.leftbubble);
                messageViewHolder.messageItem_textView_message.setText(commentList.get(position).message);
                messageViewHolder.messageItem_textView_message.setTextSize(20);
                messageViewHolder.linearLayout_main.setGravity(Gravity.START);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(uid.equals(managerUid)){ //현재 클릭한 유저가 방장이라면
                        //채팅방 참여 다이얼로그
                        AlertDialog.Builder dlg = new AlertDialog.Builder(GroupMessageActivity.this);
                        dlg.setTitle("채팅방 참여수락");
                        dlg.setMessage("채팅방 참여를 수락하시겠습니까?");
                        dlg.setPositiveButton("수락", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                //채팅참여수락
                                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("users").child(commentList.get(holder.getAdapterPosition()).uid)
                                        .setValue(true)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                //메시지 입력
                                                ChatModel.Comment comment = new ChatModel.Comment();
                                                comment.uid = managerUid;
                                                comment.message = messageViewHolder.messageItem_textview_nickName.getText().toString() +" 님 채팅참여 수락되었습니다.";
                                                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(getApplicationContext(), "채팅참여 수락완료", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });
                            }
                        });
                        dlg.setNegativeButton("거절", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //채팅참여 거절
                                    databaseReference.child("chatrooms").child(destinationRoom).child("users").child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            //메시지 입력
                                            ChatModel.Comment comment = new ChatModel.Comment();
                                            comment.uid = managerUid;
                                            comment.message = messageViewHolder.messageItem_textview_nickName.getText().toString() + " 님 채팅참여 거절되었습니다.";
                                            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(getApplicationContext(), "채팅참여 거절완료", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        dlg.create().show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return commentList.size();
        }

        private class GroupMessageViewHodler extends RecyclerView.ViewHolder {

            public TextView messageItem_textView_message;
            public TextView messageItem_textview_nickName;
            //public ImageView imageView_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;

            public GroupMessageViewHodler(View view) {
                super(view);
                messageItem_textView_message = view.findViewById(R.id.messageItem_textView_message);
                messageItem_textview_nickName = view.findViewById(R.id.messageItem_textview_nickName);
                //imageView_profile =  view.findViewById(R.id.messageItem_imageview_profile);
                linearLayout_destination =  view.findViewById(R.id.messageItem_linearlayout_destination);
                linearLayout_main =  view.findViewById(R.id.messageItem_linearlayout_main);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(valueEventListener != null) {//메시지가 없을때 꺼지는 상황을 방지
            //뒤로가기 누를때 왓칭(메시지를 다시 읽는 것을)하는것을 끔
            databaseReference.removeEventListener(valueEventListener);
        }
        finish();
        //finish밑에 들어가야 작동됨
        overridePendingTransition(R.anim.fromleft, R.anim.toright);
    }


}