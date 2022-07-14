package com.example.myapplication.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class NoticeData {
    private String type; //알림종류 C:채팅방개설, J:채팅참가요청
    private String time; //알림시간
    private String message; //알림메시지

    public NoticeData(String type, String message){
        //시간설정
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("kk:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        this.time = sdf.format(date);

        this.type = type;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
