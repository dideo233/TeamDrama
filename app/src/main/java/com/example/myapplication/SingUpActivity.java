package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SingUpActivity extends AppCompatActivity {

    //인증
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

    //회원가입
    private void signUp() {
        id= userId.getText().toString();
        pwd= userPwd.getText().toString();
        pwdCheck = userPwdCheck.getText().toString();
        nickname = userNickname.getText().toString();

        // 입력사항 체크
        if(id.length()==0) { //아이디
            Toast.makeText(SingUpActivity.this, "아이디를 입력해주세요." ,Toast.LENGTH_SHORT).show();
            userId.requestFocus(); //포커스 이동
            return;
        }
        if(pwd.length()<6) { //비밀번호
            Toast.makeText(SingUpActivity.this, "비밀번호는 6자리 이상입력해주세요." ,Toast.LENGTH_SHORT).show();
            userPwd.requestFocus(); //포커스 이동
            return;
        }
        if(pwdCheck.length()<6) { //비밀번확인
            Toast.makeText(SingUpActivity.this, "확인비밀번호를 6자리 이상입력해주세요." ,Toast.LENGTH_SHORT).show();
            userPwdCheck.requestFocus(); //포커스 이동
            return;
        }
        if(!pwd.equals(pwdCheck)) { //확인비밀번호 불일치
            Toast.makeText(SingUpActivity.this, "확인비밀번호가 일치하지 않습니다." ,Toast.LENGTH_SHORT).show();
            userPwdCheck.setText("");
            userPwdCheck.requestFocus(); //포커스 이동
            return;
        }
        if(nickname.length() == 0) { //닉네임
            Toast.makeText(SingUpActivity.this, "닉네임을 입력해 주세요" ,Toast.LENGTH_SHORT).show();
            userNickname.requestFocus(); //포커스 이동
            return;
        }

        //이메일 계정 생성
        mAuth.createUserWithEmailAndPassword(id, pwd)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //Toast.makeText(SingUpActivity.this, "회원가입에 성공했습니다." ,Toast.LENGTH_SHORT).show();
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout));

                    TextView text = (TextView) layout.findViewById(R.id.text);
                    text.setText("회원가입에 성공했습니다.");

                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();

                    //***********************USER DB저장 ***************************
                    UserModel userModel = new UserModel();

                    FirebaseUser user = mAuth.getCurrentUser();
                    userModel.setUid(user.getUid()); //Auth uid
                    userModel.setEmail(id); //아이디(이메일)
                    userModel.setNickName(nickname); //닉네임
                    userModel.setLoginKind(user.getProviderData().get(1).getProviderId()); //로그인 유형

                    //member아래 UID를 child 키로 user객체를 저장
                    FirebaseDatabase.getInstance().getReference().child("member").child(user.getUid()).setValue(userModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d("FirebaseDatabase ::: ","회원가입성공 : " + user.getEmail());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("FirebaseDatabase ::: ", "회원가입 데이터입력에러");
                        }
                    });

                    //화원가입 페이지종료(로그인페이지 이동)
                    finish();

                } else {
                    //Toast.makeText(SingUpActivity.this, "회원가입에 실패했습니다." ,Toast.LENGTH_SHORT).show();
                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout));

                    TextView text = (TextView) layout.findViewById(R.id.text);
                    text.setText("회원가입에 실패했습니다.");

                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_SHORT);
                    toast.setView(layout);
                    toast.show();
                }
            }
        });

    }

    //이메일 중복 확인 (가입 시)
    public void emailOverlapCheck(){
        id= userId.getText().toString();
        if(id.length() == 0){
            Toast.makeText(SingUpActivity.this, "이메일을 입력하세요." ,Toast.LENGTH_SHORT).show();

            //포커스 이동
            userId.requestFocus();
            return;
        }

        FirebaseDatabase.getInstance().getReference().child("member").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot item : snapshot.getChildren()) {
                    UserModel userModel = item.getValue(UserModel.class);
                    Log.d("email>>>>>>>>>>>>>", userModel.getEmail());

                    if(userModel.getEmail().equals(id)){ //이메일 중복이 있는 경우
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.toast_layout, (ViewGroup) findViewById(R.id.toast_layout));

                        TextView text = (TextView) layout.findViewById(R.id.text);
                        text.setText("이미 사용 중인 이메일입니다");

                        Toast toast = new Toast(getApplicationContext());
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setView(layout);
                        toast.show();

                        userId.setText("");
                        userId.requestFocus();
                        return;
                    }
                }

                //중복된 이메일이 없는 경우
                Log.d("select : ", "no such document");
                Toast.makeText(SingUpActivity.this, "사용 가능한 이메일입니다" ,Toast.LENGTH_SHORT).show();
                userNickname.requestFocus();
                btnSignupCheck.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("중복확인 에러 : ", "error" + error.toString());
            }
        });
    }

    //이메일 인증 (이메일 유효성 체크)
    public void sendEmailVerification() {
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // Email sent
                    }
                });
    }
}