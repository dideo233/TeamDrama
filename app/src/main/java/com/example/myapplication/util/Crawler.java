package com.example.myapplication.util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.myapplication.model.TvScheduleData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Crawler {
    String broadcastStation; //방송사
    String scheduleDate; //방송일자
    String channel;
    public Crawler(String channel){
        this.channel = channel;
    }
    //파이어베이스 데이터베이스 레퍼런스
    DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    public void tvScheduleParse() {
            try {
                    String url = "https://tv.kt.com/tv/channel/pSchedule.asp";
                    Connection conn = Jsoup.connect(url);
                Document html = conn
                            .header("Accept-Encodin", "gzip, deflate, br")
                            .data("view_type", "1")
                            .data("service_ch_no", channel)//MBC Every1 (다른 방송국 편성 정보 파싱하려면 채널 번호만 바꾸기. ex:52 spoTV2 )
                            .data("ch_type", "3")
                            .ignoreContentType(true).post();

                Elements timeH = html.select(".time:eq(0)");  //시
                Elements timeM = html.select(".time:eq(1)");  //분
                Elements program = html.select(".program");   //방송명
                Elements category = html.select(".category"); //장르

                //방송사 구분
                broadcastStation = html.select("img").attr("alt");
                Log.d("방송사 !!! ", broadcastStation); //방송사

                //날짜
                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                scheduleDate = sdf.format(date);

                for (int i = 0; i < timeH.size(); i++) {
                    String starttime = timeH.get(i).text() + ":" + timeM.get(i).text().substring(0,2);
                    //편성표 데이터를 TvScheduleData에 저장
                    TvScheduleData tvScheduleData = new TvScheduleData();
                    String title =program.get(i).text();
                    if(title.contains("%26amp;")) {
                        title = title.replace("%26amp;", "&");
                    }
                    tvScheduleData.setTitle(title);
                    tvScheduleData.setCategory(category.get(i).text());
                    tvScheduleData.setTime(starttime);
                    //데이터 베이스에 방송 1개씩 저장 (채팅방 개설을 위한 키값부여) push()를 써야 밀어넣어짐.
                    database.child("broadcast").child(broadcastStation).child(scheduleDate).push().setValue(tvScheduleData);
                }
            } catch (IOException e){
                e.printStackTrace();
            }
    }
}
