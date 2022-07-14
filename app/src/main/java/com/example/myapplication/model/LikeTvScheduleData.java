package com.example.myapplication.model;

public class LikeTvScheduleData {
    private String tvScheduleKey;
    private String broadcastStation;
    private String scheduleDate;
    private String programname;
    private String Programca;

    public String getTvScheduleKey() {
        return tvScheduleKey;
    }

    public void setTvScheduleKey(String tvScheduleKey) {
        this.tvScheduleKey = tvScheduleKey;
    }

    public String getBroadcastStation() {
        return broadcastStation;
    }

    public void setBroadcastStation(String broadcastStation) {
        this.broadcastStation = broadcastStation;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getProgramname() {
        return programname;
    }

    public void setProgramname(String programname) {
        this.programname = programname;
    }

    public String getProgramca() {
        return Programca;
    }

    public void setProgramca(String programca) {
        Programca = programca;
    }
}
