package com.example.myapplication.model;

import java.util.HashMap;
import java.util.Map;

public class ChatModel {

    public String tvScheduleKey; //방송 키값
    public String title; //채팅방 방제
    public String roomType; //채팅방 공개여부
    public String manager; //방장 uid
    public Map<String, Boolean> users = new HashMap<>(); //채팅방의 참여유저들
    public Map<String, Comment> comments = new HashMap<>(); //채팅방의 대화내용

    public static class Comment {
        public String uid;
        public String message;
    }

}
