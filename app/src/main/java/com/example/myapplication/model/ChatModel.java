package com.example.myapplication.model;

import java.util.HashMap;
import java.util.Map;

public class ChatModel {

    public String tvScheduleKey; //방송 키값
    public String tvScheduleTitle; //방송 제목
    public String openDate; // 오픈일자
    public String title; //채팅방 방제
    public String roomType; //채팅방 공개여부
    public String manager; //방장 uid
    public Map<String, Boolean> users = new HashMap<>(); //채팅방의 참여유저들
    public Map<String, Comment> comments = new HashMap<>(); //채팅방의 대화내용

    public static class Comment {
        public String uid; //로그인한 사용자 uid
        public String message; // 메시지 내용
        public String type; //메시지 타입 - G:일반메시지, J:채팅참여, A:알람메시지
        public String to; //상대방(귓말인 경우)
    }

}
