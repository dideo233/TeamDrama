package com.example.myapplication.model;

import java.util.List;

//편성표 데이터
public class TvData {
    //방송국, 방송 프로그램 데이터를 담은 리스트 객체, 편성표 일자
    String broadcastStation;
    List<TvScheduleData> tvScheduleData;
    String scheduleDate;
}
