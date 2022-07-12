package com.example.myapplication.fragment;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import com.example.myapplication.model.ChatModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailsFragment extends Fragment {
    View view;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    String myUid = mAuth.getUid();

    ImageButton like;

    TextView tvtitle,tvcategory,tvbroadcastStation;
    String programname;
    String Programca;
    String broadcastStation;
    String tvScheduleKey;

    Button btncreatechat,btnchatclose,btnjoin1,btnjoin2,btnjoin3,btnjoin4,btnjoin5;
    TextView chattop1,chattop2,chattop3,chattop4,chattop5;

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

        if (getArguments() != null)
        {
            programname = getArguments().getString("programname"); // 프래그먼트1에서 받아온 값 넣기
            Programca = getArguments().getString("Programca");
            broadcastStation = getArguments().getString("broadcastStation");
            tvScheduleKey = getArguments().getString("tvScheduleKey");

            tvtitle.setText(programname);
            tvcategory.setText(Programca);
            tvbroadcastStation.setText(broadcastStation);
        }

        //채팅방 개설버튼
        btncreatechat = view.findViewById(R.id.btncreatechat);
        btncreatechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //채팅방개설 다이얼로그
                View dialogView = v.inflate(v.getContext(), R.layout.dialog_room, null);

                EditText edt_title = dialogView.findViewById(R.id.edt_title);

                AlertDialog.Builder dlg = new AlertDialog.Builder(v.getContext());
                dlg.setTitle("채팅방개설");
                dlg.setView(dialogView);
                dlg.setPositiveButton("공개방개설", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

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
        //btnchatclose = view.findViewById(R.id.btnchatclose);
        btnjoin1 = view.findViewById(R.id.btnjoin1);
        btnjoin2 = view.findViewById(R.id.btnjoin2);
        btnjoin3 = view.findViewById(R.id.btnjoin3);
        btnjoin4 = view.findViewById(R.id.btnjoin4);
        btnjoin5 = view.findViewById(R.id.btnjoin5);
        chattop1 = view.findViewById(R.id.chattop1);
        chattop2 = view.findViewById(R.id.chattop2);
        chattop3 = view.findViewById(R.id.chattop3);
        chattop4 = view.findViewById(R.id.chattop4);
        chattop5 = view.findViewById(R.id.chattop5);

        return view;
    }


}