package com.example.myapplication.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

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
import com.example.myapplication.model.NoticeData;
import com.example.myapplication.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class GroupMessageActivity extends AppCompatActivity {
    Map<String,UserModel> members = new HashMap<>();

    String destinationRoom; //채팅방 구분키
    ChatModel chatModel; //채팅방 정보
    String managerUid; //채팅방 방장 uid

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    String uid = firebaseAuth.getCurrentUser().getUid(); //현재 접속 유저
    EditText editText;
    Button button;

    TextView title; //채팅방 제목
    TextView info; //채팅방 표시정보(채팅가능여부 등 표시)

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message);

        //메시지입력란
        editText = findViewById(R.id.groupMessageActivity_editText);
        button = findViewById(R.id.groupMessageActivity_button);

        //채팅방 구분 키
        destinationRoom = getIntent().getStringExtra("destinationRoom");

        //채팅방 제목
        title = findViewById(R.id.groupMessageActivity_textview_title);
        //채팅방 표시정보(채팅가능여부 등 표시)
        info = findViewById(R.id.groupMessageActivity_textview_info);

        //채팅방, 방장 uid 정보
        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatModel = snapshot.getValue(ChatModel.class);
                managerUid = chatModel.manager;
                title.setText(chatModel.title); //채팅방 제목
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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

                //방장이 아닌경우 채팅방 참여권한체크
                if(!uid.equals(managerUid)) {
                    checkUser();
                } else {
                    recyclerView.setAdapter(new GroupMessageRecyclerViewAdapter());
                }

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
                    if(info.getText().toString().equals("채팅가능")){
                        //메시지 입력
                        ChatModel.Comment comment = new ChatModel.Comment();
                        comment.type = "G"; //일반메시지
                        comment.uid = uid;
                        comment.message = editText.getText().toString();
                        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                editText.setText("");
                            }
                        });
                    }
                } else {
                    //메시지 입력
                    ChatModel.Comment comment = new ChatModel.Comment();
                    comment.type = "G"; //일반메시지
                    comment.uid = uid;
                    comment.message = editText.getText().toString();
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            editText.setText("");
                        }
                    });
                }


            }
        });
    }

    //채팅방 참여권한 체크
    void checkUser() {
        //채팅참여자 조회
        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("users").child(uid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) { //채팅참여자 등록
                            //채팅방 타입에 따라 채팅참여자 초기권한 제한
                            if (chatModel.roomType.equals("공개")) { //채팅방이 공개인경우
                                //채팅참여자 추가
                                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("users").child(uid).setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                       Toast.makeText(getApplicationContext(), chatModel.title + "방에 오신것을 환영합니다.", Toast.LENGTH_SHORT).show();
                                       info.setText("채팅가능");
                                       recyclerView.setAdapter(new GroupMessageRecyclerViewAdapter());
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
                                        comment.type ="J"; //채팅참여요청 메시지
                                        comment.to = managerUid; //방장에게 보냄
                                        comment.uid = uid; //보내는 사람 uid
                                        comment.message = firebaseAuth.getCurrentUser().getEmail() + "채팅참여요청\n(클릭후 수락여부 확인)";
                                        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                //채팅방 참여요청 알림
                                                NoticeData noticeData = new NoticeData("C", "[참여요청]"+title.getText().toString());
                                                FirebaseDatabase.getInstance().getReference().child("member").child(managerUid).child("notice").setValue(noticeData);

                                                Toast.makeText(getApplicationContext(), "채팅방 참여요청되었습니다.", Toast.LENGTH_SHORT).show();
                                                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("users").orderByChild(uid).equalTo(true).addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        if(snapshot.exists()) {
                                                            Toast.makeText(getApplicationContext(), "채팅방 참여가 수락되었습니다.", Toast.LENGTH_SHORT).show();
                                                            info.setText("채팅가능");
                                                            //채팅 내용 표시
                                                            recyclerView.setAdapter(new GroupMessageRecyclerViewAdapter());
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
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
                            info.setText("채팅가능");
                            //채팅 내용 표시
                            recyclerView.setAdapter(new GroupMessageRecyclerViewAdapter());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    class GroupMessageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<ChatModel.Comment>  commentList = new ArrayList<>();

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
                        if(comment.type.equals("J") && !comment.to.equals(managerUid)){
                            continue;
                        }
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

            //메시지 타입 - G:일반메시지, J:채팅참여, A:알람메시지
            String messageType = commentList.get(position).type;

            //일반메시지
            if(messageType.equals("G")) {
                //내가보낸 메세지
                if (commentList.get(position).uid.equals(uid)) {
                    if (commentList.get(position).uid.equals(managerUid)) { //방장 표시
                        messageViewHolder.messageItem_textview_nickName.setText("[방장]");
                    }
                    messageViewHolder.messageItem_textView_message.setText(commentList.get(position).message);
                    messageViewHolder.messageItem_textView_message.setBackgroundResource(R.drawable.rightbubble);
                    messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
                    messageViewHolder.messageItem_textView_message.setTextSize(20);
                    messageViewHolder.linearLayout_main.setGravity(Gravity.END);
                } else { //상대방이 보낸 메세지
                    if (commentList.get(position).uid.equals(managerUid)) { //방장표시
                        messageViewHolder.messageItem_textview_nickName.setText("[방장] " + members.get(commentList.get(position).uid).getNickName());
                    } else {
                        messageViewHolder.messageItem_textview_nickName.setText(members.get(commentList.get(position).uid).getNickName());
                    }
                    messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                    messageViewHolder.messageItem_textView_message.setBackgroundResource(R.drawable.leftbubble);
                    messageViewHolder.messageItem_textView_message.setText(commentList.get(position).message);
                    messageViewHolder.messageItem_textView_message.setTextSize(20);
                    messageViewHolder.linearLayout_main.setGravity(Gravity.START);
                }
            }

            //채팅방 참여요청 메시지(방장한테만 보냄)
            if(messageType.equals("J")) {
                if (commentList.get(position).to.equals(uid)) { //채팅 상대방(방장)이 본인
                    messageViewHolder.messageItem_textview_nickName.setText("");
                    messageViewHolder.linearLayout_main.setBackgroundColor(Color.GREEN);
                    messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                    messageViewHolder.messageItem_textView_message.setText(commentList.get(position).message);
                    messageViewHolder.messageItem_textView_message.setTextSize(16);
                    messageViewHolder.linearLayout_main.setGravity(Gravity.CENTER);

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            //채팅참여 요청수락 다이얼로그
                            AlertDialog.Builder dlg = new AlertDialog.Builder(GroupMessageActivity.this);
                            dlg.setTitle("[채팅방 참여요청]");
                            dlg.setMessage("채팅방 참여를 수락하시겠습니까?");
                            dlg.setPositiveButton("수락", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    //채팅참여수락
                                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("users").child(commentList.get(holder.getAdapterPosition()).uid).setValue(true)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(getApplicationContext(), "채팅참여 수락완료", Toast.LENGTH_SHORT).show();

                                                    //알림 - 채팅방 참여가능
                                                    NoticeData noticeData = new NoticeData("J", chatModel.title + "채팅방 참여수락" );
                                                    FirebaseDatabase.getInstance().getReference().child("member").child(commentList.get(holder.getAdapterPosition()).uid).child("notice").push().setValue(noticeData);
                                                }
                                            });
                                }
                            });
                            dlg.setNegativeButton("거절", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //채팅참여 거절
                                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(destinationRoom).child("users").child(commentList.get(holder.getAdapterPosition()).uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(getApplicationContext(), "채팅참여 거절완료", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                            dlg.create().show();
                        }
                    });
                }
            }

            //채팅방 알림 메시지(전체공지용)
            if(messageType.equals("A")) {
                messageViewHolder.linearLayout_main.setBackgroundColor(Color.BLUE);
                messageViewHolder.messageItem_textview_nickName.setText("[알림]");
                messageViewHolder.messageItem_textview_nickName.setTextColor(Color.RED);
                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.messageItem_textView_message.setTextColor(Color.WHITE);
                messageViewHolder.messageItem_textView_message.setText(commentList.get(position).message);
                messageViewHolder.messageItem_textView_message.setTextSize(20);
                messageViewHolder.linearLayout_main.setGravity(Gravity.CENTER);
            }
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