package com.example.myapplication.model;

import java.util.HashMap;
import java.util.Map;

public class UserModel {
    private String uid; //auth uid
    private String email; //이메일
    private String nickName; //닉네임
    private String loginKind; //로그인 종류
    public Map<String, Boolean> like = new HashMap<>(); //관심프로그램

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getLoginKind() {
        return loginKind;
    }

    public void setLoginKind(String loginKind) {
        this.loginKind = loginKind;
    }
}
