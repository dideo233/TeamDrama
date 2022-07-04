package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SingUpActivity extends AppCompatActivity {
    //DB
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    //인증
    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;

    //Button
    Button btnEmailCheck, btnSignupCheck;
    //사용자 정보 입력값
    EditText userId, userPwd, userPwdCheck, userNickname;
    String id, pwd, pwdCheck, nickname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_up);
        mAuth = FirebaseAuth.getInstance();

        userId = findViewById(R.id.editUserId);
        userPwd = findViewById(R.id.editUserPwd);
        userPwdCheck = findViewById(R.id.editUserPwdCheck);
        userNickname = findViewById(R.id.editUserNickname);

        btnEmailCheck = findViewById(R.id.btnEmailcheck);
        btnEmailCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailOverlapCheck();
            }
        });

        btnSignupCheck = findViewById(R.id.btnSignupCheck);
        btnSignupCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });
    }

    //id, pwd, pwdCheck 값 입력됐을 시
    //* 1.기본적으로 firebase 회원가입 시 pwd는 6자 이상)
    //* 2.email 형식 체크해주고, 중복 체크까지 해준다
    //-> 이상한 이메일을 넣어도 가입되므로 유효한 이메일인지 처리가 필요함
    //-> 회원가입 성공 및 각각의 실패 사례에 따른 처리 필요함
    private void signUp() {
        id= userId.getText().toString();
        pwd= userPwd.getText().toString();
        pwdCheck = userPwdCheck.getText().toString();
        nickname = userNickname.getText().toString();

        //* 1.기본적으로 firebase 회원가입 시 pwd는 6자 이상)
        if(id.length()==0 && pwd.length() < 6 && pwdCheck.length()<6 && nickname.length() == 0){
            Toast.makeText(SingUpActivity.this, "회원가입 데이터를 모두 입력하세요." ,Toast.LENGTH_SHORT).show();
            return;
        }

        //pwd와 pwdCheck 일치 시
        if(pwd.equals(pwdCheck)){
            //이메일 계정 생성
            mAuth.createUserWithEmailAndPassword(id, pwd)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SingUpActivity.this, "회원가입에 성공했습니다." ,Toast.LENGTH_SHORT).show();
                                FirebaseUser user = mAuth.getCurrentUser();

                                //DB 저장
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("id", user.getEmail());
                                userData.put("nickname", nickname);
                                userData.put("loginKind", user.getProviderData().get(1).getProviderId());

                                db.collection("member")
                                        .document(user.getEmail())
                                        .set(userData)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d("FirebaseFirestore : ","Document ID = " + user.getEmail());
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("FirebaseFirestore : ", "Document Error");
                                            }
                                        });

                                finish();
                            } else {
                                Toast.makeText(SingUpActivity.this, "회원가입에 실패했습니다." ,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }else{
            Toast.makeText(SingUpActivity.this, "비밀번호가 일치하지 않습니다." ,Toast.LENGTH_SHORT).show();
        }
    }

    //이메일 중복 확인 (가입 시)
    public void emailOverlapCheck(){
        id= userId.getText().toString();

        if(id.length() == 0){
            Toast.makeText(SingUpActivity.this, "이메일을 입력하세요." ,Toast.LENGTH_SHORT).show();
            return;
        }
        DocumentReference docRef = db.collection("member").document(id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){//이미 존재하는 이메일
                        Log.d("select : ", ""+document.getData());
                        Toast.makeText(SingUpActivity.this, "이미 사용 중인 이메일입니다" ,Toast.LENGTH_SHORT).show();
                    } else { //사용 가능 이메일
                        Log.d("select : ", "no such document");
                        Toast.makeText(SingUpActivity.this, "사용 가능한 이메일입니다" ,Toast.LENGTH_SHORT).show();
                        btnSignupCheck.setEnabled(true);
                    }
                } else {
                    Log.d("get failed : ", ""+task.getException());
                }
            }
        });
    }

    //이메일 인증 (이메일 유효성 체크)
    // -> 가입하고 나서 이메일 유효성 체크할 수 있도록 하는 듯.
    // -> 대개의 서비스는 이메일 유효성 체크가 완료되었는지 확인하고 나서 제공하도록 구현.
    // * 가입하면서 유효성 체크하도록 찾아보기 or 전화번호 가입 구현 생각해보기
    public void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Email sent
                    }
                });
        // [END send_email_verification]
    }
}