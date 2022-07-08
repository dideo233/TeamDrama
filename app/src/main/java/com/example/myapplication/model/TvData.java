package com.example.myapplication.model;

import java.time.LocalDate;
import java.util.List;

//편성표 데이터
public class TvData {
    //방송국, 방송 프로그램 데이터를 담은 리스트 객체, 편성표 일자
    String broadcastStation;
    List<TvScheduleData> tvScheduleData;
    String scheduleDate;

    public String getBroadcastStation() {
        return broadcastStation;
    }

    public void setBroadcastStation(String broadcastStation) {
        this.broadcastStation = broadcastStation;
    }

    public List<TvScheduleData> getTvScheduleData() {
        return tvScheduleData;
    }

    public void setTvScheduleData(List<TvScheduleData> tvScheduleData) {
        this.tvScheduleData = tvScheduleData;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }
}
