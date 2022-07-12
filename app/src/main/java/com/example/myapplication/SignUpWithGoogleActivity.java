package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.model.UserModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/*
    onCreate :
    //구글 로그인 앱에 통합
    //1.사용자 ID와 프로필 정보를 요청하기 위해서 GoogleSignInOptions 객체를 DEFAULT_SIGN_IN 인자와 함께 생성한다
    //2.gso객체를 인자로 전달해서 GoogleSignInClient 객체를 생성함 (인증 화면)
    //3.생성된 GoogleSignInClient 객체를 이용해서 intent 생성
    //4.생성된 intent를 startActivityForResult 객체로 전달하는 것으로 사용자에게 인증을 요청하는 화면이 호출된다.
    //* 사용자의 행위(로그인 등)에 따라 결과 return

    onActivityResult :
    //1.사용자가 인증 엑티비티에서 실행한 결과를 onActivityResult에서 data로 받을 수 있다.
    //2.result에는 인증 화면에서 로그인 했을 때 구글에서 넘겨주는 결과값(실행한 결과)를 받아와서 저장한다.
    //3.result가 성공했을 때, 이 값을 firebase에 넘겨주기 위해서 GoogleSignInAccount 객체로 생성한다.
    //4.firebaseAuthWithGoogle에 account값을 전달한다.
    //*signInAccount : 사용자 로그인 후 return 받은 구글 계정에 대한 정보가 담긴 객체

    firebaseAuthWithGoogle :
    //1.사용자가 성공적으로 로그인하여 해당 메소드로 account(GoogleSignInAccount 객체)를 인자로 받게 될 때, 여기서 ID 토큰을 가져와서 firebase 사용자 인증정보로 교환한다.
    //2.signInWithCredential 메소드를 통해서 firebase에 인증한다.
    //3.auth 결과값을 확인하는 부분은 앞서 이메일을 확인하는 부분과 같다.
    // 구글 계정으로 firebase 인증 시작
    //* 첫 로그인에는 authentication에 등록하고 user 객체 return, 이후 로그인에는 등록된 거 확인되면 로그인으로 판별하고 user 객체 리턴?

 */
public class SignUpWithGoogleActivity extends AppCompatActivity {
    public static Context mContext;

    //인증
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private String TAG="mainTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //구글 로그인으로 받은 사용자 정보를 firebase 인증 (GoogleSignInAccount : 로그인된 구글 계정 정보가 담긴 객체)
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential) //사용자의 인증 정보를 firebase로 넘긴다
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) { //로그인 성공
                        Log.d(TAG, "signInWithCredential:success");
                        Toast.makeText(getApplicationContext(), "Authentication Success", Toast.LENGTH_LONG).show();

                        //***********************USER DB저장 ***************************
                        UserModel userModel = new UserModel();

                        FirebaseUser user = mAuth.getCurrentUser();
                        userModel.setUid(user.getUid()); //Auth uid
                        userModel.setEmail(user.getEmail()); //아이디(이메일)
                        userModel.setNickName(user.getDisplayName()); //닉네임
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

                        //메인화면전환
                        updateUI(user);

                    } else { //실패
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(getApplicationContext(), "Authentication Failed", Toast.LENGTH_LONG).show();
                        //updateUI(null);
                    }
                }
            });
    }

    //로그인 유무 확인 -> 로그인 상태일 때 바로 홈 화면으로
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    public void updateUI(FirebaseUser user) {
        if(user != null){
            Log.d("로그인된 계정(이메일) : ", ""+mAuth.getCurrentUser().getEmail());
            Intent intent = new Intent(SignUpWithGoogleActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    //로그아웃
    public void signOut() {
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
            new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "signOut Complete", Toast.LENGTH_LONG).show();
                }
            });
    }

    //기존 계정과의 연결 해제. 다른 계정으로 로그인하려고 할 때
    public void revokeAccess() {
        mAuth.signOut();
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(this,
            new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "revokeAccess Complete", Toast.LENGTH_LONG).show();
                }
            });
    }
}