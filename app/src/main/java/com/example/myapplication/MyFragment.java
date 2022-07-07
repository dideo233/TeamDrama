package com.example.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.myapplication.util.Crawler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.google.firebase.firestore.Transaction;


public class MyFragment extends Fragment {
    FirebaseUser user;
    String signEmail;
    DocumentSnapshot document;
    String nicknames;
    String nick;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    DocumentReference docRef ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            return;
        }

//        user.getProviderId();
//        Log.d("pro : ", user.getProviderId());

        //Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        //startActivity(intent);


////        //대충 테스트
//        Log.d("제공 : ", user.getProviderId());
//        Log.d("provider Data :",user.getProviderData().toString());
//        for(UserInfo profile : user.getProviderData()){
//            Log.d("pro : ", profile.getProviderId());
//            Log.d("Provider-specific UID:", profile.getEmail());
//            Log.d("Name:", profile.getEmail());
//        }
//
//        TextView tvUserEmail = findViewById(R.id.tvUserEmail);
//        tvUserEmail.setText(user.getEmail());
//        TextView tvUserProvider = findViewById(R.id.tvUserProvider);
//        tvUserProvider.setText(user.getProviderData().get(1).getProviderId());

//        TextView tvDisplayName = findViewById(R.id.tvDisplayName);
//        tvDisplayName.setText(user.getDisplayName());




        //
//        signEmail = user.getEmail();
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        DocumentReference docRef = db.collection("member").document(signEmail);
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    document = task.getResult();
//                    if (document.exists()) {
//                        loginKind=document.getData().get("loginKind");
//                        nicknames=document.getData().get("nickname");
//                        Log.d("loginKind", "구글: "+loginKind);
//                        Log.d("nickname", "닉네임: "+nicknames);
//                    } else {
//                        Log.d("TAG", "No such document");
//                    }
//                } else {
//                    Log.d("TAG", "get failed with ", task.getException());
//                }
//            }
//        });
//        nick = String.valueOf(nicknames);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_my,container,false);
        Button btnLogout = (Button)rootview.findViewById(R.id.btnLogout);
        Button btnSignout = (Button)rootview.findViewById(R.id.btnSignout);
        ImageButton btnNickChange = (ImageButton)rootview.findViewById(R.id.nickChange);
        TextView nickname = (TextView)rootview.findViewById(R.id.tvnickname);

        signEmail = user.getEmail();

        docRef = db.collection("member").document(signEmail);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    document = task.getResult();
                    if (document.exists()) {
                        nicknames=String.valueOf(document.getData().get("nickname"));
                        Log.d("nickname", "닉네임즈: "+nicknames);
                        if (nicknames.length()>=7){
                            String longnick = nicknames.substring(0,5)+"...";
                            nickname.setText(longnick);
                        }else{
                            nickname.setText(nicknames);
                        }
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });



        //로그아웃 (* 리스너?)
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        //구글 계정 연결 끊기
        btnSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //다른 액티비티의 메소드를 호출하기 위해 new 키워드로 액티비티 생성하는 것은 불가
                //https://soo0100.tistory.com/1266 참고

                ((SignUpWithGoogleActivity)SignUpWithGoogleActivity.mContext).revokeAccess();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
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
                        db.runTransaction(new Transaction.Function<Void>() {
                                    @Override
                                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                                        //닉네임 변경
                                        transaction.update(docRef, "nickname", newnick);
                                        return null;
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("TAG", "Transaction success!");
                                        nickname.setText(newnick);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("TAG", "Transaction failure.", e);
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



}