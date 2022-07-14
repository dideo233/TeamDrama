package com.example.myapplication.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.LoginActivity;
import com.example.myapplication.R;
import com.example.myapplication.SignUpWithGoogleActivity;
import com.example.myapplication.model.LikeTvScheduleData;
import com.example.myapplication.model.NoticeData;
import com.example.myapplication.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyFragment extends Fragment {

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference databaseReference;

    UserModel userData; //사용자 정보

    RecyclerView fragmentmy_recyclerview_notice;
    RecyclerView fragmentmy_recyclerview_like;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(user == null){  //로그인된 유저가 없는 경우
            return;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_my, container, false);
        Button btnLogout = (Button) rootview.findViewById(R.id.btnLogout);
        Button btnSignout = (Button) rootview.findViewById(R.id.btnSignout);
        Button btnNoticeRemove = rootview.findViewById(R.id.fragmentmy_button_noticeRemove);
        ImageButton btnNickChange = (ImageButton) rootview.findViewById(R.id.nickChange);
        TextView nickname = (TextView) rootview.findViewById(R.id.tvnickname);

        fragmentmy_recyclerview_notice = rootview.findViewById(R.id.fragmentmy_recyclerview_notice);
        fragmentmy_recyclerview_notice.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        fragmentmy_recyclerview_notice.setAdapter(new MyNoticeRecyclerViewAdapter());

        fragmentmy_recyclerview_like = rootview.findViewById(R.id.fragmentmy_recyclerview_like);
        fragmentmy_recyclerview_like.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        fragmentmy_recyclerview_like.setAdapter(new MyLikeRecyclerViewAdapter());

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("member").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userData = snapshot.getValue(UserModel.class);

                String nick = userData.getNickName();
                Log.d("nickname", "닉네임: " + nick);

                if (nick.length() > 6) {
                    String longnick = nick.substring(0, 5) + "...";
                    nickname.setText(longnick);
                } else {
                    nickname.setText(nick);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("nickname 에러", "error: " + error.toString());
            }
        });

        //로그아웃
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        //구글 계정 연결 끊기
        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //다른 액티비티의 메소드를 호출하기 위해 new 키워드로 액티비티 생성하는 것은 불가
                //https://soo0100.tistory.com/1266 참고

                ((SignUpWithGoogleActivity) SignUpWithGoogleActivity.mContext).revokeAccess();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });

        //알림메시지 정리
        btnNoticeRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().child("member").child(user.getUid()).child("notice").removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "알림메시지 정리완료", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        //닉네임 수정 버튼
        btnNickChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View dialogView = view.inflate(view.getContext(), R.layout.nick_change, null);

                EditText edtnick = dialogView.findViewById(R.id.edtnick);

                android.app.AlertDialog.Builder dlg = new AlertDialog.Builder(view.getContext());
                dlg.setTitle("닉네임 변경");
                dlg.setView(dialogView);
                dlg.setPositiveButton("변경", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String newnick = edtnick.getText().toString();

                        //database 업데이트는 해쉬맵형태로 (키값, 바꿀내용)
                        Map<String, Object> updateData = new HashMap<>();
                        updateData.put("nickName", newnick); //키값 대소문자 주의

                        databaseReference.child("member").child(user.getUid()).updateChildren(updateData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d("닉네임변경", "Transaction success!");
                                nickname.setText(newnick);
                            }
                        });
                    }
                });
                dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                });
                dlg.show();
            }
        });
        return rootview;
    }

    //최근 알림 어뎁터
    class MyNoticeRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<NoticeData> myNoticeList = new ArrayList<>();

        public MyNoticeRecyclerViewAdapter() {
            FirebaseDatabase.getInstance().getReference().child("member").child(user.getUid()).child("notice").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        for(DataSnapshot item : snapshot.getChildren()){
                            NoticeData noticeData = item.getValue(NoticeData.class);
                            myNoticeList.add(noticeData);
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            CustomViewHolder customViewHolder = (CustomViewHolder) holder;

            if(myNoticeList.get(position).getType().equals("C")){
                customViewHolder.noticeitem_imageview.setImageResource(R.drawable.notice);
            } else if(myNoticeList.get(position).getType().equals("J")){
                customViewHolder.noticeitem_imageview.setImageResource(R.drawable.user_join);
            }

            customViewHolder.noticeitem_textview_time.setText(myNoticeList.get(position).getTime());
            customViewHolder.noticeitem_textview_message.setText(myNoticeList.get(position).getMessage());

        }

        @Override
        public int getItemCount() {
            return myNoticeList == null ? 0 : myNoticeList.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {

            ImageView noticeitem_imageview; //알림종류별 이미지
            TextView noticeitem_textview_time; //알림시간
            TextView noticeitem_textview_message; //알림내용

            public CustomViewHolder(View view) {
                super(view);
                noticeitem_imageview = view.findViewById(R.id.noticeitem_imageview);
                noticeitem_textview_time = view.findViewById(R.id.noticeitem_textview_time);
                noticeitem_textview_message = view.findViewById(R.id.noticeitem_textview_message);
            }
        }
    }

    //관심 프로그램 어뎁터
    class MyLikeRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<LikeTvScheduleData> myLikeList = new ArrayList<>();

        public MyLikeRecyclerViewAdapter() {
            FirebaseDatabase.getInstance().getReference().child("member").child(user.getUid()).child("like").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    myLikeList.clear();
                    if(snapshot.exists()){
                        for(DataSnapshot item : snapshot.getChildren()){
                            LikeTvScheduleData likeTvScheduleData = item.getValue(LikeTvScheduleData.class);
                            myLikeList.add(likeTvScheduleData);
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_like, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            CustomViewHolder customViewHolder = (CustomViewHolder) holder;

            customViewHolder.likeItem_textview_info.setText(myLikeList.get(position).getBroadcastStation());
            customViewHolder.likeItem_textview_title.setText(myLikeList.get(position).getProgramname());

            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }

        @Override
        public int getItemCount() {
            return myLikeList == null ? 0 : myLikeList.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {

            TextView likeItem_textview_info; //채팅방 개설정보
            TextView likeItem_textview_title; //프로그램 타이틀

            public CustomViewHolder(View view) {
                super(view);
                likeItem_textview_info = view.findViewById(R.id.likeItem_textview_info);
                likeItem_textview_title = view.findViewById(R.id.likeItem_textview_title);
            }
        }
    }
}
